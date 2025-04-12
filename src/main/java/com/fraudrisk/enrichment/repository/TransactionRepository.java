package com.fraudrisk.enrichment.repository;

import com.fraudrisk.enrichment.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, String> {

    List<TransactionEntity> findByCustomerIdOrderByTransactionDateDesc(String customerId);

    List<TransactionEntity> findByMerchantIdOrderByTransactionDateDesc(String merchantId);

    @Query("SELECT t FROM TransactionEntity t WHERE t.customerId = :customerId AND t.transactionDate >= :startDate")
    List<TransactionEntity> findCustomerTransactionsAfterDate(
            @Param("customerId") String customerId,
            @Param("startDate") Instant startDate
    );

    @Query("SELECT COUNT(t) FROM TransactionEntity t WHERE t.customerId = :customerId AND t.transactionDate >= :startDate")
    long countCustomerTransactionsAfterDate(
            @Param("customerId") String customerId,
            @Param("startDate") Instant startDate
    );

    @Query("SELECT AVG(t.amount) FROM TransactionEntity t WHERE t.customerId = :customerId AND t.transactionDate >= :startDate")
    Double getAverageTransactionAmount(
            @Param("customerId") String customerId,
            @Param("startDate") Instant startDate
    );
}