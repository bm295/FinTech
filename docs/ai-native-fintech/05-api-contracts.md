# API Contracts (Phase 1)

## Conventions
- REST JSON over HTTPS.
- Idempotency key required for mutation endpoints involving funds movement.
- Correlation ID propagated in request/response headers.

## Implemented allocation preview

### POST /api/payments/allocations
Calculates proportional allocations without initiating payments or changing balances.

- Required: nonnegative `amount`, supported ISO 4217 `currency` with defined minor units,
  and a nonempty array of positive whole-number `weights`.
- Amount must be exactly representable at the currency's precision; extra trailing zeros are accepted.
- Returns `amount`, `currency`, GCD-reduced `weights`, and `allocations` in recipient order.
- Allocations sum exactly to the amount. Largest remainders receive leftover minor units;
  ties favor earlier recipients.
- Invalid inputs return HTTP 400 with problem details including `status` and `detail`.

Example request:

```json
{"amount":50.00,"currency":"USD","weights":[1200,1800]}
```

Example response:

```json
{"amount":50.00,"currency":"USD","weights":[2,3],"allocations":[20.00,30.00]}
```

## Identity & onboarding

### POST /v1/auth/register
Creates user identity.

### POST /v1/kyc/submissions
Submits KYC payload and documents.

### GET /v1/kyc/status/{userId}
Returns current KYC status and latest review reason.

## Wallets

### GET /v1/wallets
List user wallets.

### POST /v1/wallets
Create wallet (if allowed by policy).

### GET /v1/wallets/{walletId}/balance
Returns available + held balances and last ledger sequence.

## Payments

### POST /v1/payments
Initiate payment.
- Required: source wallet, destination, amount, currency, idempotency key.
- Returns payment state and risk precheck status.

### GET /v1/payments/{paymentId}
Returns payment status timeline.

### POST /v1/payments/{paymentId}/cancel
Attempts cancellation if state allows.

## Risk operations

### GET /v1/risk/cases
List review queue cases (operator role).

### POST /v1/risk/cases/{caseId}/decision
Submit manual decision with reason codes.

## AI endpoints

### POST /v1/ai/customer-assistant/query
Natural language assistant for customer.

### POST /v1/ai/operator-assistant/case-summary
Generates case summary and suggested action.

## Error model
For the planned `/v1` APIs, all errors include:
- `code`
- `message`
- `correlation_id`
- `details` (optional)
