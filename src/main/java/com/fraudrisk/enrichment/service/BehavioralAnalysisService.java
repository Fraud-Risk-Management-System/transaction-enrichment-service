package com.fraudrisk.enrichment.service;

import com.fraudrisk.enrichment.model.Transaction;
import com.fraudrisk.enrichment.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class BehavioralAnalysisService {

    private final TransactionRepository transactionRepository;

    public Map<String, Object> analyzeTransaction(Transaction transaction) {
        log.debug("Analyzing behavioral patterns for transaction: {}", transaction.getTransactionId());

        Map<String, Object> features = new HashMap<>();

        try {
            String customerId = transaction.getCustomerId();

            // Calculate time periods for analysis
            Instant now = Instant.now();
            Instant oneDayAgo = now.minus(Duration.ofDays(1));
            Instant oneWeekAgo = now.minus(Duration.ofDays(7));
            Instant oneMonthAgo = now.minus(Duration.ofDays(30));

            // Transaction count features
            long transactionCount24h = transactionRepository.countCustomerTransactionsAfterDate(customerId, oneDayAgo);
            long transactionCount7d = transactionRepository.countCustomerTransactionsAfterDate(customerId, oneWeekAgo);
            long transactionCount30d = transactionRepository.countCustomerTransactionsAfterDate(customerId, oneMonthAgo);

            features.put("transactionCount24h", transactionCount24h);
            features.put("transactionCount7d", transactionCount7d);
            features.put("transactionCount30d", transactionCount30d);

            // Average transaction amount features
            Double avgAmount24h = transactionRepository.getAverageTransactionAmount(customerId, oneDayAgo);
            Double avgAmount7d = transactionRepository.getAverageTransactionAmount(customerId, oneWeekAgo);
            Double avgAmount30d = transactionRepository.getAverageTransactionAmount(customerId, oneMonthAgo);

            features.put("avgAmount24h", avgAmount24h != null ? avgAmount24h : 0.0);
            features.put("avgAmount7d", avgAmount7d != null ? avgAmount7d : 0.0);
            features.put("avgAmount30d", avgAmount30d != null ? avgAmount30d : 0.0);

            // Transaction velocity (transactions per hour)
            double velocity24h = transactionCount24h / 24.0;
            double velocity7d = transactionCount7d / (24.0 * 7);

            features.put("velocity24h", velocity24h);
            features.put("velocity7d", velocity7d);

            // Amount deviation from average
            BigDecimal currentAmount = transaction.getAmount();
            if (avgAmount30d != null && avgAmount30d > 0) {
                double amountDeviation = (currentAmount.doubleValue() - avgAmount30d) / avgAmount30d;
                features.put("amountDeviation", amountDeviation);
            } else {
                features.put("amountDeviation", 0.0);
            }

            log.debug("Behavioral analysis completed for transaction: {}", transaction.getTransactionId());
        } catch (Exception e) {
            log.error("Error in behavioral analysis for transaction {}: {}",
                    transaction.getTransactionId(), e.getMessage(), e);
            // Add default values in case of error
            features.put("error", e.getMessage());
            features.put("transactionCount24h", 0);
            features.put("transactionCount7d", 0);
            features.put("transactionCount30d", 0);
            features.put("avgAmount24h", 0.0);
            features.put("avgAmount7d", 0.0);
            features.put("avgAmount30d", 0.0);
            features.put("velocity24h", 0.0);
            features.put("velocity7d", 0.0);
            features.put("amountDeviation", 0.0);
        }

        return features;
    }
}