package com.fraudrisk.enrichment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Entity
@Table(name = "customers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEntity {

    @Id
    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "customer_type")
    private String customerType;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "tenure_months")
    private Integer customerTenureMonths;

    @Column(name = "risk_category")
    private String riskCategory;

    @Column(name = "kyc_status")
    private String kycStatus;

    @Column(name = "country_of_residence")
    private String countryOfResidence;

    @Column(name = "last_login_time")
    private Instant lastLoginTime;

    @Column(name = "account_creation_date")
    private Instant accountCreationDate;

    @Column(name = "has_previous_fraud")
    private Boolean hasPreviousFraud;

    @Column(name = "activity_level")
    private Integer activityLevel;
}