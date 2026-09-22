package com.websiteroute.application;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public interface TransactionService {
    TransactionResult initiateTransaction(TransactionRequest request);
    TransactionStatus getTransactionStatus(String transactionId);
    List<TransactionSummary> getRecentTransactions(String accountId, int pageSize);
    record TransactionRequest(String sourceAccountId, String destinationAccountId, BigDecimal amount, String currency, String description) {}
    record TransactionResult(String transactionId, boolean success, String failureReason) {}
    record TransactionStatus(String transactionId, String status, OffsetDateTime createdAt, OffsetDateTime completedAt, String reason) {}
    record TransactionSummary(String transactionId, BigDecimal amount, String currency, String status, OffsetDateTime createdAt, String description) {}
}
