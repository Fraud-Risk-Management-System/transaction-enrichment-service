package com.fraudrisk.enrichment.service;

import com.fraudrisk.enrichment.model.EnrichedTransaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionProducerService {

    private final KafkaTemplate<String, EnrichedTransaction> kafkaTemplate;

    @Value("${kafka.topics.enriched}")
    private String enrichedTransactionsTopic;

    public void publishEnrichedTransaction(EnrichedTransaction enrichedTransaction) {
        String transactionId = enrichedTransaction.getTransaction().getTransactionId();
        log.debug("Publishing enriched transaction: {}", transactionId);

        try {
            kafkaTemplate.send(enrichedTransactionsTopic, transactionId, enrichedTransaction).get();
            log.debug("Successfully published enriched transaction: {}", transactionId);
        } catch (Exception e) {
            log.error("Error publishing enriched transaction {}: {}", transactionId, e.getMessage(), e);
            throw new RuntimeException("Failed to publish enriched transaction", e);
        }
    }
}
