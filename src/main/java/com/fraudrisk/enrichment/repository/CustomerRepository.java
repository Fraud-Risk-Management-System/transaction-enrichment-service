package com.fraudrisk.enrichment.repository;

import com.fraudrisk.enrichment.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, String> {
    // Custom query methods can be added here
}