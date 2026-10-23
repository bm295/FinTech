# FinTech (Java / Spring Boot)

This repository contains the Java foundation for a FinTech application with a
feature-first payment module and Clean Architecture dependency direction.

## Current domain

The application layer defines the transaction service contract for initiating transactions, checking transaction status, and retrieving recent transactions for an account.

Transaction records use `BigDecimal` for monetary amounts and `OffsetDateTime` for timestamps.

Proportional payment allocation uses the Euclidean GCD remainder loop from the
`restaurant(l, b)` square-tiling example to reduce recipient weights. For example,
`1200:1800` becomes `2:3`, so `50.00 USD` allocates as `20.00` and `30.00`.
Calculations use arbitrary-precision integers in currency minor units. Leftover
units go to the largest fractional remainders; ties favor earlier recipients.

## Source structure

```text
src/main/java/com/websiteroute/
|-- WebSiteRouteApplication.java
`-- payment/
    |-- domain/
    |   |-- Payment.java
    |   |-- PaymentAllocation.java
    |   `-- PaymentAllocator.java
    |-- application/
    |   |-- PaymentActivityService.java
    |   |-- PaymentAllocationService.java
    |   `-- TransactionService.java
    `-- adapter/in/web/
        |-- PaymentActivityController.java
        `-- PaymentAllocationController.java
```

Dependencies point inward: HTTP adapter -> application -> domain. The domain
depends only on Java. Application services retain Spring `@Service` for component
scanning, so the application layer is not fully framework independent. Tests mirror
these packages under `src/test/java`; static content remains in
`src/main/resources/static`.

External integrations can introduce application-owned ports in
`payment/application/port/out` and implementations in `payment/adapter/out` when
needed. The current implementation has no database or payment gateway integration.
See [the architecture document](docs/ai-native-fintech/03-system-architecture.md)
for the current implementation and longer-term target.

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

GitHub Actions runs the tests for pull requests targeting `master` and pushes to `master`.
Code coverage appears directly in the workflow run summary and the **Show code coverage**
step logs, with no artifact download required.

Generate the same JaCoCo coverage report locally:

```bash
mvn clean verify
```

Open `target/site/jacoco/index.html` to inspect the local coverage report.

Start the application:

```bash
mvn spring-boot:run
```

Open the frontend at [http://localhost:8080](http://localhost:8080). It displays the payment activity and the completed sample payment of `50.00 USD`.

The backend endpoint is:

```http
GET http://localhost:8080/api/payments
```

Preview a proportional payment allocation:

```http
POST http://localhost:8080/api/payments/allocations
Content-Type: application/json

{"amount":50.00,"currency":"USD","weights":[1200,1800]}
```

Response:

```json
{"amount":50.00,"currency":"USD","weights":[2,3],"allocations":[20.00,30.00]}
```

Weights and allocations retain recipient order. Splitting `0.05 USD` with weights
`[1,1,1]` returns `[0.02,0.02,0.01]`, preserving the full amount. The endpoint
calculates a preview without initiating a payment. It accepts nonnegative amounts
representable in the currency's minor units and positive whole-number weights.
Invalid inputs return HTTP 400 with a problem detail response.

To create and run a packaged application instead:

```bash
mvn clean package
java -jar target/fintech-1.0.0.jar
```

The default port is `8080`. Override it when needed:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=9090"
```
