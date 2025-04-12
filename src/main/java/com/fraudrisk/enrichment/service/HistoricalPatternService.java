package com.fraudrisk.enrichment.service;

import com.fraudrisk.enrichment.model.Transaction;
import com.fraudrisk.enrichment.entity.TransactionEntity;
import com.fraudrisk.enrichment.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class HistoricalPatternService {

    private final TransactionRepository transactionRepository;

    public Map<String, Object> getHistoricalPatterns(Transaction transaction) {
        log.debug("Analyzing historical patterns for transaction: {}", transaction.getTransactionId());

        Map<String, Object> patterns = new HashMap<>();

        try {
            String customerId = transaction.getCustomerId();
            String merchantId = transaction.getMerchantId();
            String merchantCategory = transaction.getMerchantCategory();
            BigDecimal amount = transaction.getAmount();
            Instant transactionDate = transaction.getTransactionDate();
            LocalDateTime localTransactionTime = LocalDateTime.ofInstant(transactionDate, ZoneId.systemDefault());

            // Get customer's historical transactions from the last 6 months
            List<TransactionEntity> customerHistory = transactionRepository
                    .findCustomerTransactionsAfterDate(
                            customerId,
                            Instant.now().minus(180, ChronoUnit.DAYS)
                    );

            if (customerHistory.isEmpty()) {
                patterns.put("noHistory", true);
                patterns.put("historyRiskScore", 0.7); // Higher risk for no history
                return patterns;
            }

            // Check if customer has transacted with this merchant before
            boolean hasTransactedWithMerchant = customerHistory.stream()
                    .anyMatch(t -> merchantId.equals(t.getMerchantId()));
            patterns.put("hasTransactedWithMerchant", hasTransactedWithMerchant);

            // Check if customer has transacted in this merchant category before
            boolean hasTransactedInCategory = customerHistory.stream()
                    .anyMatch(t -> merchantCategory.equals(t.getMerchantCategory()));
            patterns.put("hasTransactedInCategory", hasTransactedInCategory);

            // Get typical transaction amount
            double avgAmount = customerHistory.stream()
                    .map(TransactionEntity::getAmount)
                    .mapToDouble(BigDecimal::doubleValue)
                    .average()
                    .orElse(0.0);
            patterns.put("averageAmount", avgAmount);

            // Calculate amount deviation from average
            double amountDeviation = amount.doubleValue() / avgAmount - 1.0;
            patterns.put("amountDeviation", amountDeviation);

            // Typical transaction day and time patterns
            Map<DayOfWeek, Long> dayOfWeekCounts = customerHistory.stream()
                    .collect(Collectors.groupingBy(
                            t -> LocalDateTime.ofInstant(t.getTransactionDate(), ZoneId.systemDefault()).getDayOfWeek(),
                            Collectors.counting()
                    ));

            // Get most common day of week
            DayOfWeek mostCommonDay = dayOfWeekCounts.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null);
            patterns.put("mostCommonDay", mostCommonDay != null ? mostCommonDay.toString() : "UNKNOWN");

            // Check if current transaction is on a typical day
            boolean isTypicalDay = mostCommonDay == localTransactionTime.getDayOfWeek();
            patterns.put("isTypicalDay", isTypicalDay);

            // Time of day analysis
            int hourOfDay = localTransactionTime.getHour();
            boolean isNightTime = hourOfDay >= 22 || hourOfDay <= 5;
            patterns.put("isNightTime", isNightTime);

            // Calculate overall pattern risk score
            double patternRiskScore = calculatePatternRiskScore(
                    hasTransactedWithMerchant,
                    hasTransactedInCategory,
                    amountDeviation,
                    isTypicalDay,
                    isNightTime
            );
            patterns.put("patternRiskScore", patternRiskScore);

            log.debug("Historical pattern analysis completed for transaction: {}", transaction.getTransactionId());
        } catch (Exception e) {
            log.error("Error in historical pattern analysis for transaction {}: {}",
                    transaction.getTransactionId(), e.getMessage(), e);
            patterns.put("error", e.getMessage());
            patterns.put("patternRiskScore", 0.5);
            patterns.put("hasTransactedWithMerchant", false);
            patterns.put("hasTransactedInCategory", false);
        }

        return patterns;
    }

    private double calculatePatternRiskScore(
            boolean hasTransactedWithMerchant,
            boolean hasTransactedInCategory,
            double amountDeviation,
            boolean isTypicalDay,
            boolean isNightTime) {

        double score = 0.3; // Base score

        // New merchant/category is higher risk
        if (!hasTransactedWithMerchant) score += 0.2;
        if (!hasTransactedInCategory) score += 0.1;

        // Unusual amount is higher risk
        if (Math.abs(amountDeviation) > 2.0) score += 0.2;
        else if (Math.abs(amountDeviation) > 1.0) score += 0.1;

        // Unusual day/time is higher risk
        if (!isTypicalDay) score += 0.1;
        if (isNightTime) score += 0.1;

        // Cap at 1.0
        return Math.min(score, 1.0);
    }
}