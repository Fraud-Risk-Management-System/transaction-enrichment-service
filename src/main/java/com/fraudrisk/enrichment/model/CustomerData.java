package com.fraudrisk.enrichment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerData {
    private String customerId;
    private String customerType;
    private Integer customerTenureMonths;
    private String customerRiskCategory;
    private String kycStatus;
    private String countryOfResidence;
    private Instant lastLoginTime;
    private Instant accountCreationDate;
    private Boolean hasPreviousFraud;
    private Integer accountActivityLevel;
}