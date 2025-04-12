package com.fraudrisk.enrichment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantData {
    private String merchantId;
    private String merchantName;
    private String merchantCategory;
    private String merchantCountry;
    private String merchantRiskLevel;
    private Boolean isHighRiskMerchant;
    private Boolean isNewMerchant;
    private Integer merchantTenureDays;
    private Float fraudRatePercentage;
}