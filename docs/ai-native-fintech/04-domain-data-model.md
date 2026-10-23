# Domain and Data Model

## Core entities

### User
- `user_id` (UUID)
- `email`, `phone`, `status`
- `risk_tier`
- timestamps

### KYCProfile
- `kyc_id`, `user_id`
- `verification_status`
- `document_refs`
- `review_notes`

### Wallet
- `wallet_id`, `user_id`, `currency`
- `available_balance`, `held_balance`
- `state`

### LedgerAccount
- `ledger_account_id`
- `owner_type` (user/system/fee/reserve)
- `currency`

### LedgerEntry
- `entry_id`, `posting_group_id`
- `debit_account_id`, `credit_account_id`
- `amount`, `currency`
- `reference_type`, `reference_id`
- `created_at`

### Payment
- `payment_id`, `initiator_user_id`
- `source_wallet_id`, `destination_type`
- `amount`, `currency`, `fee_amount`
- `status`, `rail_type`, `external_ref`

### RiskDecision
- `decision_id`, `payment_id`
- `score`, `decision` (allow/review/deny)
- `reasons[]`, `model_version`

### PaymentAllocation (calculated preview)
- `amount`, `currency`
- GCD-reduced positive integer `weights` and corresponding `allocations`, in recipient order.
- The allocator uses Euclid's remainder loop to simplify weights and distributes exact minor units
  using largest remainders, with ties resolved by recipient order.
- Allocations preserve the total amount and use the currency's precision.

### Case
- `case_id`, `entity_type`, `entity_id`
- `queue`, `assignee`, `status`
- `ai_summary`, `resolution_notes`

## Data integrity rules
- Monetary fields use decimal fixed-point with currency-aware precision.
- Every payment settlement must map to a balanced posting group.
- No deletion of ledger entries; correction via compensating entries.
- PII fields are tagged for encryption/tokenization policy.
