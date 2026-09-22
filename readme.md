# FinTech (Java / Spring Boot)

This repository contains the Java foundation for a FinTech application using Clean Architecture.

## Current domain

The application layer defines the transaction service contract for initiating transactions, checking transaction status, and retrieving recent transactions for an account.

Transaction records use `BigDecimal` for monetary amounts and `OffsetDateTime` for timestamps.

## Requirements

- Java 21+
- Maven 3.9+

## Run

```bash
mvn spring-boot:run
```

Build and run with `mvn package` followed by `java -jar target/fintech-1.0.0.jar`.
