package com.websiteroute.payment.domain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;

public final class PaymentAllocator {
    /**
     * Reduces proportional weights using Euclid's algorithm and splits an amount
     * into exact currency minor units. Largest remainders receive leftover units;
     * ties are resolved by the original recipient order.
     */
    public PaymentAllocation allocate(BigDecimal amount, Currency currency, List<BigInteger> weights) {
        if (amount == null || currency == null || weights == null) {
            throw new IllegalArgumentException("Amount, currency and weights are required.");
        }
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("Amount must not be negative.");
        }
        int scale = currency.getDefaultFractionDigits();
        if (scale < 0) {
            throw new IllegalArgumentException("Currency must define a minor unit precision.");
        }

        BigDecimal normalizedAmount;
        try {
            normalizedAmount = amount.setScale(scale, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException exception) {
            throw new IllegalArgumentException(
                    "Amount exceeds the precision for " + currency.getCurrencyCode() + ".", exception);
        }

        weights = new ArrayList<>(weights);
        if (weights.isEmpty()) {
            throw new IllegalArgumentException("At least one weight is required.");
        }
        BigInteger divisor = BigInteger.ZERO;
        for (BigInteger weight : weights) {
            if (weight == null || weight.signum() <= 0) {
                throw new IllegalArgumentException("Weights must be positive whole numbers.");
            }
            divisor = gcd(divisor, weight);
        }

        List<BigInteger> reducedWeights = new ArrayList<>();
        BigInteger totalWeight = BigInteger.ZERO;
        for (BigInteger weight : weights) {
            BigInteger reduced = weight.divide(divisor);
            reducedWeights.add(reduced);
            totalWeight = totalWeight.add(reduced);
        }

        BigInteger minorUnits = normalizedAmount.unscaledValue();
        BigInteger assignedUnits = BigInteger.ZERO;
        List<BigInteger> shares = new ArrayList<>();
        List<Remainder> remainders = new ArrayList<>();
        for (int index = 0; index < reducedWeights.size(); index++) {
            BigInteger[] share = minorUnits.multiply(reducedWeights.get(index)).divideAndRemainder(totalWeight);
            shares.add(share[0]);
            assignedUnits = assignedUnits.add(share[0]);
            remainders.add(new Remainder(index, share[1]));
        }

        remainders.sort(Comparator.comparing(Remainder::value).reversed().thenComparingInt(Remainder::index));
        // The sum of fractional remainders is smaller than the number of recipients.
        int leftoverUnits = minorUnits.subtract(assignedUnits).intValueExact();
        for (int index = 0; index < leftoverUnits; index++) {
            int recipient = remainders.get(index).index();
            shares.set(recipient, shares.get(recipient).add(BigInteger.ONE));
        }

        List<BigDecimal> allocations = shares.stream().map(share -> new BigDecimal(share, scale)).toList();
        return new PaymentAllocation(normalizedAmount, currency, reducedWeights, allocations);
    }

    private static BigInteger gcd(BigInteger a, BigInteger b) {
        // The same Euclidean remainder loop used in the restaurant square-tiling snippet.
        while (b.signum() != 0) {
            BigInteger remainder = a.remainder(b);
            a = b;
            b = remainder;
        }
        return a;
    }

    private record Remainder(int index, BigInteger value) {}
}
