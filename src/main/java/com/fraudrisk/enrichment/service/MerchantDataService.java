package com.fraudrisk.enrichment.service;

import com.fraudrisk.enrichment.model.MerchantData;
import com.fraudrisk.enrichment.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MerchantDataService {

    private final MerchantRepository merchantRepository;

    @Cacheable(value = "merchantData", key = "#merchantId")
    public MerchantData getMerchantData(String merchantId) {
        log.debug("Fetching merchant data for: {}", merchantId);

        // Retrieve merchant from repository
        return merchantRepository.findById(merchantId)
                .map(merchant -> MerchantData.builder()
                        .merchantId(merchant.getMerchantId())
                        .merchantName(merchant.getMerchantName())
                        .merchantCategory(merchant.getMerchantCategory())
                        .merchantCountry(merchant.getMerchantCountry())
                        .merchantRiskLevel(merchant.getRiskLevel())
                        .isHighRiskMerchant(merchant.isHighRisk())
                        .isNewMerchant(merchant.getTenureDays() < 90)
                        .merchantTenureDays(merchant.getTenureDays())
                        .fraudRatePercentage(merchant.getFraudRatePercentage())
                        .build())
                .orElse(createDefaultMerchantData(merchantId));
    }

    private MerchantData createDefaultMerchantData(String merchantId) {
        log.warn("Merchant data not found for ID: {}, using default values", merchantId);

        return MerchantData.builder()
                .merchantId(merchantId)
                .merchantName("UNKNOWN")
                .merchantCategory("UNKNOWN")
                .merchantCountry("UNKNOWN")
                .merchantRiskLevel("HIGH")
                .isHighRiskMerchant(true)
                .isNewMerchant(true)
                .merchantTenureDays(0)
                .fraudRatePercentage(0.0f)
                .build();
    }
}