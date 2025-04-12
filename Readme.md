# Transaction Enrichment Service

This service is responsible for enriching transaction data with additional information for fraud detection purposes. It consumes raw transaction data from Kafka, enriches it with customer data, merchant information, risk scores, and behavioral analysis, and then publishes the enriched data back to Kafka for downstream processing.

## Architecture

The Transaction Enrichment Service is part of a larger fraud detection system with the following components:

- **Transaction Ingestion Service**: Receives transaction data from external systems and publishes it to Kafka
- **Transaction Enrichment Service** (this service): Enriches transaction data with additional information
- **Risk Scoring Service**: Calculates risk scores for transactions based on various factors
- **Fraud Detection Service**: Makes final determinations on whether transactions are fraudulent

### Data Flow

1. Raw transactions are consumed from the `banking-transactions` Kafka topic
2. Various enrichment services add additional data:
    - Customer data (from database)
    - Merchant data (from database)
    - Risk scores (from risk scoring service)
    - Behavioral analysis
    - Geo-location analysis
    - Device analysis
    - Historical pattern analysis
3. Enriched transactions are published to the `enriched-transactions` Kafka topic
4. Any processing errors are published to the `transaction-processing-errors` topic

## Features

- **Parallel Data Enrichment**: Uses CompletableFuture for concurrent processing
- **Caching**: Redis caching for frequently accessed data
- **Resilience**: Circuit breakers for external service calls
- **Error Handling**: Comprehensive error handling with fallbacks
- **Monitoring**: Actuator endpoints for health checks and metrics

## Technologies

- Java 17
- Spring Boot 3.2.3
- Spring Kafka
- Spring Data JPA
- Spring Data Redis
- PostgreSQL
- Redis
- Avro for schema management
- Resilience4j for circuit breaking
- Micrometer for metrics

## Getting Started

### Prerequisites

- Docker and Docker Compose
- Java 17+
- Maven

### Running Locally

1. Clone the repository
2. Build the service:
   ```
   ./mvnw clean package
   ```
3. Start the required infrastructure using Docker Compose:
   ```
   docker-compose up -d zookeeper kafka schema-registry postgres redis
   ```
4. Run the service:
   ```
   ./mvnw spring-boot:run
   ```

### Running with Docker Compose

To start the entire system including all services:

```
docker-compose up -d
```

## Configuration

The service can be configured using the following environment variables:

- `SPRING_KAFKA_BOOTSTRAP_SERVERS`: Kafka broker addresses
- `SPRING_KAFKA_CONSUMER_PROPERTIES_SCHEMA_REGISTRY_URL`: Schema Registry URL
- `SPRING_DATASOURCE_URL`: PostgreSQL database URL
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password
- `SPRING_REDIS_HOST`: Redis host
- `RISK_SERVICE_URL`: Risk Scoring Service URL

See `application.yml` for additional configuration options.

## Monitoring

The service exposes the following Actuator endpoints:

- Health check: `/actuator/health`
- Metrics: `/actuator/metrics`
- Prometheus: `/actuator/prometheus`

## Development

### Adding New Enrichment Features

To add a new enrichment feature:

1. Create a new service class in the `com.fraudrisk.enrichment.service` package
2. Add the feature to the `EnrichmentService.enrichTransaction` method
3. Update the `EnrichedTransaction` model to include the new data

### Testing

Run tests with:

```
./mvnw test
```

Integration tests use Testcontainers to spin up required infrastructure.

## Production Considerations

For production deployment, consider:

- Setting up multiple instances for high availability
- Configuring Kafka consumer group settings for proper scaling
- Tuning JVM and connection pool settings
- Setting up proper monitoring and alerting
- Implementing robust schema evolution strategies