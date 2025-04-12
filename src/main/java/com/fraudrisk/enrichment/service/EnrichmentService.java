package com.fraudrisk.enrichment.service;

import com.fraudrisk.enrichment.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class EnrichmentService {

    private final CustomerDataService customerDataService;
    private final MerchantDataService merchantDataService;
    private final RiskScoringService riskScoringService;
    private final BehavioralAnalysisService behavioralAnalysisService;
    private final GeoAnalysisService geoAnalysisService;
    private final DeviceAnalysisService deviceAnalysisService;
    private final HistoricalPatternService historicalPatternService;

    public EnrichedTransaction enrichTransaction(Transaction transaction) {
        log.debug("Enriching transaction: {}", transaction.getTransactionId());

        // Parallelize data retrieval for performance
        CompletableFuture<CustomerData> customerDataFuture =
                CompletableFuture.supplyAsync(() -> customerDataService.getCustomerData(transaction.getCustomerId()));

        CompletableFuture<MerchantData> merchantDataFuture =
                CompletableFuture.supplyAsync(() -> merchantDataService.getMerchantData(transaction.getMerchantId()));

        CompletableFuture<Map<String, Object>> behavioralFeaturesFuture =
                CompletableFuture.supplyAsync(() -> behavioralAnalysisService.analyzeTransaction(transaction));

        CompletableFuture<Map<String, Object>> geoFeaturesFuture =
                CompletableFuture.supplyAsync(() -> geoAnalysisService.analyzeLocation(transaction));

        CompletableFuture<Map<String, Object>> deviceFeaturesFuture =
                CompletableFuture.supplyAsync(() -> deviceAnalysisService.analyzeDevice(transaction));

        CompletableFuture<Map<String, Object>> historicalPatternsFuture =
                CompletableFuture.supplyAsync(() -> historicalPatternService.getHistoricalPatterns(transaction));

        // Wait for all futures to complete
        CompletableFuture.allOf(
                customerDataFuture,
                merchantDataFuture,
                behavioralFeaturesFuture,
                geoFeaturesFuture,
                deviceFeaturesFuture,
                historicalPatternsFuture
        ).join();

        // Extract results from futures
        CustomerData customerData = customerDataFuture.join();
        MerchantData merchantData = merchantDataFuture.join();
        Map<String, Object> behavioralFeatures = behavioralFeaturesFuture.join();
        Map<String, Object> geoFeatures = geoFeaturesFuture.join();
        Map<String, Object> deviceFeatures = deviceFeaturesFuture.join();
        Map<String, Object> historicalPatterns = historicalPatternsFuture.join();

        // Calculate risk score based on all the enriched data
        RiskScore riskScore = riskScoringService.calculateRiskScore(
                transaction,
                customerData,
                merchantData,
                behavioralFeatures,
                geoFeatures,
                deviceFeatures,
                historicalPatterns
        );

        // Build and return the enriched transaction
        return EnrichedTransaction.builder()
                .transaction(transaction)
                .customerData(customerData)
                .merchantData(merchantData)
                .riskScore(riskScore)
                .behavioralFeatures(behavioralFeatures)
                .geoFeatures(geoFeatures)
                .deviceFeatures(deviceFeatures)
                .historicalPatterns(historicalPatterns)
                .additionalFeatures(new HashMap<>())
                .build();
    }
}