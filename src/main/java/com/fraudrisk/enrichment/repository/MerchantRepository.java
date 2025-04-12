package com.fraudrisk.enrichment.repository;

import com.fraudrisk.enrichment.entity.MerchantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantRepository extends JpaRepository<MerchantEntity, String> {
    // Custom query methods can be added here
}