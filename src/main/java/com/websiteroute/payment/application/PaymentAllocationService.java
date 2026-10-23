package com.websiteroute.payment.application;

import com.websiteroute.payment.domain.PaymentAllocation;
import com.websiteroute.payment.domain.PaymentAllocator;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PaymentAllocationService {
    private final PaymentAllocator allocator = new PaymentAllocator();

    public PaymentAllocation allocate(BigDecimal amount, String currencyCode, List<BigDecimal> weights) {
        if (currencyCode == null || weights == null) {
            throw new IllegalArgumentException("Currency and weights are required.");
        }
        Currency currency;
        try {
            currency = Currency.getInstance(currencyCode);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Currency must be a supported ISO 4217 code.", exception);
        }

        List<BigInteger> integerWeights = new ArrayList<>();
        for (BigDecimal weight : weights) {
            if (weight == null) {
                throw new IllegalArgumentException("Weights must be positive whole numbers.");
            }
            try {
                integerWeights.add(weight.toBigIntegerExact());
            } catch (ArithmeticException exception) {
                throw new IllegalArgumentException("Weights must be positive whole numbers.", exception);
            }
        }
        return allocator.allocate(amount, currency, integerWeights);
    }
}
