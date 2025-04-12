package com.fraudrisk.enrichment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.time.Duration;

@SpringBootApplication
@EnableCaching
public class TransactionEnrichmentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransactionEnrichmentServiceApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public CircuitBreakerRegistry circuitBreakerRegistry() {
		CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
				.failureRateThreshold(50)
				.waitDurationInOpenState(Duration.ofMillis(10000))
				.permittedNumberOfCallsInHalfOpenState(2)
				.slidingWindowSize(10)
				.build();

		return CircuitBreakerRegistry.of(circuitBreakerConfig);
	}
}