using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace WebSiteRoute.Application.Abstractions;

public interface ITransactionService
{
    Task<TransactionResult> InitiateTransactionAsync(TransactionRequest request);

    Task<TransactionStatus> GetTransactionStatusAsync(string transactionId);

    Task<IEnumerable<TransactionSummary>> GetRecentTransactionsAsync(string accountId, int pageSize = 20);
}

public sealed record TransactionRequest(
    string SourceAccountId,
    string DestinationAccountId,
    decimal Amount,
    string Currency,
    string Description);

public sealed record TransactionResult(
    string TransactionId,
    bool Success,
    string? FailureReason = null);

public sealed record TransactionStatus(
    string TransactionId,
    string Status,
    DateTimeOffset CreatedAt,
    DateTimeOffset? CompletedAt = null,
    string? Reason = null);

public sealed record TransactionSummary(
    string TransactionId,
    decimal Amount,
    string Currency,
    string Status,
    DateTimeOffset CreatedAt,
    string? Description = null);
