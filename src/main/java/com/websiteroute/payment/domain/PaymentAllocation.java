package com.websiteroute.payment.domain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Currency;
import java.util.List;

public record PaymentAllocation(
        BigDecimal amount,
        Currency currency,
        List<BigInteger> weights,
        List<BigDecimal> allocations) {
    public PaymentAllocation {
        weights = List.copyOf(weights);
        allocations = List.copyOf(allocations);
    }
}
