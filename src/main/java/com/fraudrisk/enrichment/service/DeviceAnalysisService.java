package com.fraudrisk.enrichment.service;

import com.fraudrisk.enrichment.entity.TransactionEntity;
import com.fraudrisk.enrichment.model.Transaction;
import com.fraudrisk.enrichment.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeviceAnalysisService {

    private final TransactionRepository transactionRepository;

    public Map<String, Object> analyzeDevice(Transaction transaction) {
        log.debug("Analyzing device data for transaction: {}", transaction.getTransactionId());

        Map<String, Object> deviceFeatures = new HashMap<>();

        try {
            String deviceId = transaction.getDeviceId();
            String customerId = transaction.getCustomerId();

            if (deviceId == null || deviceId.isEmpty()) {
                deviceFeatures.put("deviceMissing", true);
                deviceFeatures.put("deviceRiskScore", 0.8); // Higher risk for missing device
                return deviceFeatures;
            }

            // Get customer's recent transactions
            List<TransactionEntity> recentTransactions = transactionRepository
                    .findCustomerTransactionsAfterDate(
                            customerId,
                            Instant.now().minus(90, ChronoUnit.DAYS)
                    );

            // Extract device usage patterns
            List<String> customerDevices = recentTransactions.stream()
                    .map(TransactionEntity::getDeviceId)
                    .distinct()
                    .collect(Collectors.toList());

            int deviceCount = customerDevices.size();
            boolean isKnownDevice = customerDevices.contains(deviceId);

            deviceFeatures.put("deviceCount90d", deviceCount);
            deviceFeatures.put("isKnownDevice", isKnownDevice);

            // Device first seen date
            Instant deviceFirstSeen = recentTransactions.stream()
                    .filter(t -> deviceId.equals(t.getDeviceId()))
                    .map(TransactionEntity::getTransactionDate)
                    .min(Instant::compareTo)
                    .orElse(Instant.now());

            long deviceAgeInDays = ChronoUnit.DAYS.between(deviceFirstSeen, Instant.now());
            deviceFeatures.put("deviceAgeInDays", deviceAgeInDays);

            // New device risk score
            double deviceRiskScore = calculateDeviceRiskScore(isKnownDevice, deviceAgeInDays, deviceCount);
            deviceFeatures.put("deviceRiskScore", deviceRiskScore);

            // Device consistency score (how consistently the customer uses this device)
            long transactionsWithThisDevice = recentTransactions.stream()
                    .filter(t -> deviceId.equals(t.getDeviceId()))
                    .count();

            double deviceConsistency = (double) transactionsWithThisDevice / recentTransactions.size();
            deviceFeatures.put("deviceConsistency", deviceConsistency);

            log.debug("Device analysis completed for transaction: {}", transaction.getTransactionId());
        } catch (Exception e) {
            log.error("Error in device analysis for transaction {}: {}",
                    transaction.getTransactionId(), e.getMessage(), e);
            deviceFeatures.put("error", e.getMessage());
            deviceFeatures.put("deviceRiskScore", 0.5);
            deviceFeatures.put("isKnownDevice", false);
            deviceFeatures.put("deviceCount90d", 1);
        }

        return deviceFeatures;
    }

    private double calculateDeviceRiskScore(boolean isKnownDevice, long deviceAgeInDays, int deviceCount) {
        double baseScore = isKnownDevice ? 0.2 : 0.8;

        // Adjust for device age
        if (deviceAgeInDays < 1) {
            baseScore += 0.3; // Brand new device is higher risk
        } else if (deviceAgeInDays < 7) {
            baseScore += 0.1; // Relatively new device
        }

        // Adjust for device count (many devices could be suspicious)
        if (deviceCount > 5) {
            baseScore += 0.2;
        }

        // Cap the score at 1.0
        return Math.min(baseScore, 1.0);
    }
}
