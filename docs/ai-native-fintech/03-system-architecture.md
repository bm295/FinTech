# Target System Architecture

## Current implementation

The repository currently runs as one Spring Boot application with a feature-first
payment module under `com.websiteroute.payment`. `WebSiteRouteApplication` stays
in the root `com.websiteroute` package, allowing component scanning to discover the
payment services and controllers. Resources remain under `src/main/resources`.

- `payment.domain` contains the payment records and allocation rules, including
  Euclidean GCD weight reduction and exact allocation in currency minor units.
  It depends only on the Java standard library.
- `payment.application` contains payment activity and allocation services and the
  existing transaction service contract. The activity service currently provides
  sample data; the transaction contract has no implementation yet.
- `payment.adapter.in.web` contains the HTTP controllers, request/response models,
  and HTTP error mapping. It exposes `GET /api/payments` and
  `POST /api/payments/allocations`.

Source dependencies point inward:

```text
payment.adapter.in.web -> payment.application -> payment.domain
```

The web adapter may use domain types to map responses. Application services retain
Spring `@Service` annotations for existing wiring, so the application layer is not
fully framework independent. There are no additional interfaces or configuration
classes solely to enforce the folder layout. Tests mirror the production packages
under `src/test/java`.

When an external integration is needed, define its port in
`payment.application.port.out` and implement it in `payment.adapter.out`. These
packages are added with the integration rather than as empty placeholders. The
current code has no database, external payment rail, or event broker integration.
The remaining sections describe the target architecture as these capabilities
are introduced.

## Architectural style
- Modular monolith in early phase, designed with clear service boundaries for gradual extraction.
- Event-driven integration for money movement lifecycle and audit.

## Core components
1. **API Gateway / BFF**
   - AuthN/AuthZ enforcement.
   - Request shaping and rate limits.
2. **Identity Service**
   - User profile, auth, role mapping.
3. **KYC Service**
   - Workflow orchestration with external verification vendors.
4. **Wallet Service**
   - Wallet metadata and available/held balances.
5. **Ledger Service**
   - Double-entry journal, immutable postings.
6. **Payment Service**
   - Payment instruction lifecycle + external rail adapters.
7. **Risk Service**
   - Rule engine + model scoring.
8. **AI Orchestration Service**
   - Prompt assembly, tool calls, guardrails, response policy checks.
9. **Notification Service**
   - Email/SMS/in-app messaging.
10. **Observability + Audit Pipeline**
   - Centralized logs, traces, immutable audit stream.

## Data stores
- Relational DB (OLTP): users, wallets, payments, cases.
- Ledger DB/schema (append-only entries).
- Cache: session/token/rate limiting.
- Object storage: statements, compliance evidence artifacts.
- Vector index (optional): support AI retrieval over internal policy docs.

## Event topics
- `kyc.status.changed`
- `wallet.balance.changed`
- `ledger.posted`
- `payment.status.changed`
- `risk.case.created`
- `ai.decision.logged`

## Trust boundaries
- Public API boundary.
- Internal service boundary.
- Sensitive processing zone (PII, payment keys).

## Deployment strategy
- Containerized workloads.
- Blue/green or canary release for risk and payment modules.
- Separate environments: dev, staging, production with strict secret isolation.
