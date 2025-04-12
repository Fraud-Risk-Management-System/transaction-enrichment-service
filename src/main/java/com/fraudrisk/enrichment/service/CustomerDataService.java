package com.fraudrisk.enrichment.service;

import com.fraudrisk.enrichment.model.CustomerData;
import com.fraudrisk.enrichment.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerDataService {

    private final CustomerRepository customerRepository;

    @Cacheable(value = "customerData", key = "#customerId")
    public CustomerData getCustomerData(String customerId) {
        log.debug("Fetching customer data for: {}", customerId);

        // Retrieve customer from repository
        return customerRepository.findById(customerId)
                .map(customer -> CustomerData.builder()
                        .customerId(customer.getCustomerId())
                        .customerType(customer.getCustomerType())
                        .customerTenureMonths(customer.getCustomerTenureMonths())
                        .customerRiskCategory(customer.getRiskCategory())
                        .kycStatus(customer.getKycStatus())
                        .countryOfResidence(customer.getCountryOfResidence())
                        .lastLoginTime(customer.getLastLoginTime())
                        .accountCreationDate(customer.getAccountCreationDate())
                        .hasPreviousFraud(customer.getHasPreviousFraud())
                        .accountActivityLevel(customer.getActivityLevel())
                        .build())
                .orElse(createDefaultCustomerData(customerId));
    }

    private CustomerData createDefaultCustomerData(String customerId) {
        log.warn("Customer data not found for ID: {}, using default values", customerId);

        return CustomerData.builder()
                .customerId(customerId)
                .customerType("UNKNOWN")
                .customerTenureMonths(0)
                .customerRiskCategory("HIGH")
                .kycStatus("UNKNOWN")
                .countryOfResidence("UNKNOWN")
                .lastLoginTime(Instant.now())
                .accountCreationDate(Instant.now())
                .hasPreviousFraud(false)
                .accountActivityLevel(0)
                .build();
    }
}