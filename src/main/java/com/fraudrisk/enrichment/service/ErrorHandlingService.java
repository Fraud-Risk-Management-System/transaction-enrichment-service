package com.fraudrisk.enrichment.service;

import com.fraudrisk.enrichment.model.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ErrorHandlingService {

    private final KafkaTemplate<String, Map<String, Object>> kafkaTemplate;

    @Value("${kafka.topics.errors}")
    private String errorsTopic;

    public void handleProcessingError(Transaction transaction, Exception exception) {
        String transactionId = transaction.getTransactionId();
        log.error("Handling error for transaction: {}", transactionId, exception);

        Map<String, Object> errorMessage = new HashMap<>();
        errorMessage.put("transactionId", transactionId);
        errorMessage.put("timestamp", System.currentTimeMillis());
        errorMessage.put("errorMessage", exception.getMessage());
        errorMessage.put("errorType", exception.getClass().getSimpleName());
        errorMessage.put("transaction", transaction);

        try {
            kafkaTemplate.send(errorsTopic, transactionId, errorMessage).get();
            log.debug("Published error message for transaction: {}", transactionId);
        } catch (Exception e) {
            log.error("Failed to publish error message for transaction {}: {}",
                    transactionId, e.getMessage(), e);
        }
    }
}