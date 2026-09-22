# FinTech (Java / Spring Boot)

This repository contains the Java foundation for a FinTech application using Clean Architecture.

## Current domain

The application layer defines the transaction service contract for initiating transactions, checking transaction status, and retrieving recent transactions for an account.

Transaction records use `BigDecimal` for monetary amounts and `OffsetDateTime` for timestamps.

## Requirements

- Java 21+
- Maven 3.9+

## Run locally

Clone the repository and open a terminal at the repository root:

```bash
git clone <repository-url>
cd FinTech
```

Verify the local toolchain:

```bash
java -version
mvn -version
```

Run the automated tests:

```bash
mvn test
```

Start the application:

```bash
mvn spring-boot:run
```

Open the frontend at [http://localhost:8080](http://localhost:8080). It displays the payment activity and the completed sample payment of `50.00 USD`.

The backend endpoint is:

```http
GET http://localhost:8080/api/payments
```

To create and run a packaged application instead:

```bash
mvn clean package
java -jar target/fintech-1.0.0.jar
```

The default port is `8080`. Override it when needed:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=9090"
```
