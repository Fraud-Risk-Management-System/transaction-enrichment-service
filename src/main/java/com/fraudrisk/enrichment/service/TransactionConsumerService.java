package com.fraudrisk.enrichment.service;

import com.fraudrisk.enrichment.model.Transaction;
import com.fraudrisk.enrichment.model.EnrichedTransaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionConsumerService {

    private final EnrichmentService enrichmentService;
    private final TransactionProducerService producerService;
    private final ErrorHandlingService errorHandlingService;

    @KafkaListener(topics = "${kafka.topics.input}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeTransaction(Transaction transaction) {
        log.debug("Received transaction: {}", transaction.getTransactionId());

        try {
            // Enrich the transaction with additional data
            EnrichedTransaction enrichedTransaction = enrichmentService.enrichTransaction(transaction);

            // Publish the enriched transaction to the output topic
            producerService.publishEnrichedTransaction(enrichedTransaction);

            log.debug("Successfully processed transaction: {}", transaction.getTransactionId());
        } catch (Exception e) {
            log.error("Error processing transaction {}: {}", transaction.getTransactionId(), e.getMessage(), e);
            errorHandlingService.handleProcessingError(transaction, e);
        }
    }
}