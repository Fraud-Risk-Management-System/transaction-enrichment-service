package com.fraudrisk.enrichment.service;

import com.fraudrisk.enrichment.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class RiskScoringService {

    private final RestTemplate restTemplate;

    @Value("${risk-service.url}")
    private String riskServiceUrl;

    @CircuitBreaker(name = "riskScoringService", fallbackMethod = "calculateFallbackRiskScore")
    public RiskScore calculateRiskScore(
            Transaction transaction,
            CustomerData customerData,
            MerchantData merchantData,
            Map<String, Object> behavioralFeatures,
            Map<String, Object> geoFeatures,
            Map<String, Object> deviceFeatures,
            Map<String, Object> historicalPatterns) {

        log.debug("Calculating risk score for transaction: {}", transaction.getTransactionId());

        try {
            // Create the request payload with all the data
            Map<String, Object> riskRequest = new HashMap<>();
            riskRequest.put("transaction", transaction);
            riskRequest.put("customerData", customerData);
            riskRequest.put("merchantData", merchantData);
            riskRequest.put("behavioralFeatures", behavioralFeatures);
            riskRequest.put("geoFeatures", geoFeatures);
            riskRequest.put("deviceFeatures", deviceFeatures);
            riskRequest.put("historicalPatterns", historicalPatterns);

            // Call the external risk scoring service
            RiskScore riskScore = restTemplate.postForObject(
                    riskServiceUrl,
                    riskRequest,
                    RiskScore.class
            );

            if (riskScore != null) {
                log.debug("Risk score calculated for transaction {}: {}",
                        transaction.getTransactionId(), riskScore.getOverallScore());
                return riskScore;
            } else {
                log.error("Received null risk score from service for transaction: {}",
                        transaction.getTransactionId());
                return createDefaultRiskScore(transaction);
            }
        } catch (Exception e) {
            log.error("Error calling risk scoring service for transaction {}: {}",
                    transaction.getTransactionId(), e.getMessage(), e);
            throw e;
        }
    }

    public RiskScore calculateFallbackRiskScore(
            Transaction transaction,
            CustomerData customerData,
            MerchantData merchantData,
            Map<String, Object> behavioralFeatures,
            Map<String, Object> geoFeatures,
            Map<String, Object> deviceFeatures,
            Map<String, Object> historicalPatterns,
            Exception e) {

        log.warn("Using fallback risk scoring for transaction {}: {}",
                transaction.getTransactionId(), e.getMessage());

        // Simple fallback logic - could be enhanced with a local ML model
        double riskScore = 0.5; // Medium risk by default

        // Adjust based on few key factors
        if (customerData.getHasPreviousFraud()) {
            riskScore += 0.3;
        }

        if (merchantData.getIsHighRiskMerchant()) {
            riskScore += 0.2;
        }

        if (merchantData.getIsNewMerchant()) {
            riskScore += 0.1;
        }

        // Cap the score at 1.0
        riskScore = Math.min(riskScore, 1.0);

        // Create the risk score object
        String riskLevel = riskScore >= 0.7 ? "HIGH" : (riskScore >= 0.4 ? "MEDIUM" : "LOW");

        Map<String, Double> componentScores = new HashMap<>();
        componentScores.put("fallback", riskScore);

        return RiskScore.builder()
                .overallScore(riskScore)
                .identityRiskScore(riskScore)
                .behavioralRiskScore(riskScore)
                .transactionRiskScore(riskScore)
                .deviceRiskScore(riskScore)
                .geoLocationRiskScore(riskScore)
                .componentScores(componentScores)
                .riskLevel(riskLevel)
                .riskReason("Calculated using fallback logic due to service unavailability")
                .build();
    }

    private RiskScore createDefaultRiskScore(Transaction transaction) {
        log.warn("Creating default risk score for transaction: {}", transaction.getTransactionId());

        Map<String, Double> componentScores = new HashMap<>();
        componentScores.put("default", 0.5);

        return RiskScore.builder()
                .overallScore(0.5)
                .identityRiskScore(0.5)
                .behavioralRiskScore(0.5)
                .transactionRiskScore(0.5)
                .deviceRiskScore(0.5)
                .geoLocationRiskScore(0.5)
                .componentScores(componentScores)
                .riskLevel("MEDIUM")
                .riskReason("Default risk score")
                .build();
    }
}