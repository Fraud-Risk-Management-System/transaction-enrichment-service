package com.fraudrisk.enrichment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "merchants")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantEntity {

    @Id
    @Column(name = "merchant_id")
    private String merchantId;

    @Column(name = "merchant_name")
    private String merchantName;

    @Column(name = "merchant_category")
    private String merchantCategory;

    @Column(name = "merchant_country")
    private String merchantCountry;

    @Column(name = "risk_level")
    private String riskLevel;

    @Column(name = "is_high_risk")
    private boolean isHighRisk;

    @Column(name = "tenure_days")
    private Integer tenureDays;

    @Column(name = "fraud_rate_percentage")
    private Float fraudRatePercentage;
}
