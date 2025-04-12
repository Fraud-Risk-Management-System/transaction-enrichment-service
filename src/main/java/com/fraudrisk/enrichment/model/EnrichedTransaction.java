package com.fraudrisk.enrichment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrichedTransaction {
    // Original transaction data
    private Transaction transaction;

    // Enriched data
    private CustomerData customerData;
    private MerchantData merchantData;
    private RiskScore riskScore;
    private Map<String, Object> behavioralFeatures;
    private Map<String, Object> geoFeatures;
    private Map<String, Object> deviceFeatures;
    private Map<String, Object> historicalPatterns;
    private Map<String, Object> additionalFeatures;
}