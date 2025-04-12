package com.fraudrisk.enrichment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskScore {
    private Double overallScore;
    private Double identityRiskScore;
    private Double behavioralRiskScore;
    private Double transactionRiskScore;
    private Double deviceRiskScore;
    private Double geoLocationRiskScore;
    private Map<String, Double> componentScores;
    private String riskLevel; // HIGH, MEDIUM, LOW
    private String riskReason;
}