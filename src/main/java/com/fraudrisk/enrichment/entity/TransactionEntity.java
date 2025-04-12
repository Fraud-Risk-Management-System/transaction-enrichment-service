package com.fraudrisk.enrichment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEntity {

    @Id
    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "account_id")
    private String accountId;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "merchant_id")
    private String merchantId;

    @Column(name = "merchant_name")
    private String merchantName;

    @Column(name = "merchant_category")
    private String merchantCategory;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "currency")
    private String currency;

    @Column(name = "card_type")
    private String cardType;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "transaction_type")
    private String transactionType;

    @Column(name = "transaction_status")
    private String transactionStatus;

    @Column(name = "transaction_date")
    private Instant transactionDate;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "location")
    private String location;
}