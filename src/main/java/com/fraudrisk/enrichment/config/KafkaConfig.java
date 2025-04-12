package com.fraudrisk.enrichment.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.ExponentialBackOff;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.topics.enriched}")
    private String enrichedTransactionsTopic;

    @Value("${kafka.topics.errors}")
    private String errorsTopic;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic enrichedTransactionsTopic() {
        return new NewTopic(enrichedTransactionsTopic, 3, (short) 1);
    }

    @Bean
    public NewTopic errorsTopic() {
        return new NewTopic(errorsTopic, 1, (short) 1);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
            ConsumerFactory<Object, Object> consumerFactory) {

        ConcurrentKafkaListenerContainerFactory<Object, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);

        // Configure concurrent consumers
        factory.setConcurrency(3);

        // Configure manual acknowledgment
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        // Configure error handling with exponential backoff
        ExponentialBackOff backOff = new ExponentialBackOff(1000L, 2.0);
        backOff.setMaxInterval(10000L); // 10 seconds max
        backOff.setMaxElapsedTime(300000L); // 5 minutes total

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(backOff);
        errorHandler.setCommitRecovered(true);

        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }
}