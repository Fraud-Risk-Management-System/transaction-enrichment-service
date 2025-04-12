package com.fraudrisk.enrichment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private String transactionId;
    private String accountId;
    private String customerId;
    private String merchantId;
    private String merchantName;
    private String merchantCategory;
    private BigDecimal amount;
    private String currency;
    private String cardType;
    private String paymentMethod;
    private String transactionType;
    private String transactionStatus;
    private Instant transactionDate;
    private String deviceId;
    private String ipAddress;
    private String location;
}