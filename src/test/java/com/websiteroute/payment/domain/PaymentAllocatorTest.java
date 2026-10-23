package com.websiteroute.payment.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class PaymentAllocatorTest {
    private static final Currency USD = Currency.getInstance("USD");
    private final PaymentAllocator allocator = new PaymentAllocator();

    @Test
    void reducesWeightsAndAllocatesThePaymentInOriginalOrder() {
        PaymentAllocation result = allocator.allocate(new BigDecimal("50.00"), USD,
                List.of(BigInteger.valueOf(1200), BigInteger.valueOf(1800)));

        assertThat(result.amount()).isEqualByComparingTo("50.00");
        assertThat(result.currency()).isEqualTo(USD);
        assertThat(result.weights()).containsExactly(BigInteger.TWO, BigInteger.valueOf(3));
        assertAllocations(result, "20.00", "30.00");
    }

    @Test
    void awardsTiedRemaindersToEarlierRecipients() {
        PaymentAllocation result = allocator.allocate(new BigDecimal("0.05"), USD,
                List.of(BigInteger.ONE, BigInteger.ONE, BigInteger.ONE));

        assertAllocations(result, "0.02", "0.02", "0.01");
    }

    @Test
    void awardsTheExtraCentToTheLargestRemainderRatherThanTheLargestWeight() {
        PaymentAllocation result = allocator.allocate(new BigDecimal("0.02"), USD,
                List.of(BigInteger.ONE, BigInteger.TWO));

        assertAllocations(result, "0.01", "0.01");
    }

    @Test
    void acceptsZeroAmount() {
        PaymentAllocation result = allocator.allocate(BigDecimal.ZERO, USD,
                List.of(BigInteger.TWO, BigInteger.valueOf(3)));

        assertAllocations(result, "0.00", "0.00");
    }

    @Test
    void givesTheFullAmountToASingleRecipient() {
        PaymentAllocation result = allocator.allocate(new BigDecimal("19.99"), USD,
                List.of(BigInteger.valueOf(27)));

        assertThat(result.weights()).containsExactly(BigInteger.ONE);
        assertAllocations(result, "19.99");
    }

    @ParameterizedTest
    @CsvSource({"JPY, 5, 3, 2", "KWD, 0.005, 0.003, 0.002"})
    void usesTheCurrencyMinorUnit(String currencyCode, String amount,
            String firstAllocation, String secondAllocation) {
        PaymentAllocation result = allocator.allocate(new BigDecimal(amount),
                Currency.getInstance(currencyCode), List.of(BigInteger.ONE, BigInteger.ONE));

        assertAllocations(result, firstAllocation, secondAllocation);
    }

    @Test
    void acceptsTrailingZerosBeyondTheCurrencyPrecision() {
        PaymentAllocation result = allocator.allocate(new BigDecimal("1.2300"), USD,
                List.of(BigInteger.ONE, BigInteger.TWO));

        assertAllocations(result, "0.41", "0.82");
    }

    @Test
    void handlesAmountsAndCoprimeWeightsBeyondLongRange() {
        BigInteger weight = BigInteger.TEN.pow(40);
        List<BigInteger> weights = List.of(weight, weight.add(BigInteger.ONE));

        PaymentAllocation result = allocator.allocate(
                new BigDecimal("100000000000000000000000.01"), USD, weights);

        assertThat(result.weights()).containsExactlyElementsOf(weights);
        assertAllocations(result, "50000000000000000000000.00", "50000000000000000000000.01");
    }

    @ParameterizedTest
    @CsvSource({"USD, -0.01", "USD, 0.001", "JPY, 0.1", "KWD, 0.0001"})
    void rejectsNegativeAmountsAndInexactMinorUnits(String currencyCode, String amount) {
        assertThatIllegalArgumentException().isThrownBy(() -> allocator.allocate(
                new BigDecimal(amount), Currency.getInstance(currencyCode), List.of(BigInteger.ONE)));
    }

    @Test
    void rejectsCurrenciesWithoutDefinedMinorUnits() {
        assertThatIllegalArgumentException().isThrownBy(() -> allocator.allocate(
                BigDecimal.ONE, Currency.getInstance("XXX"), List.of(BigInteger.ONE)));
    }

    @Test
    void rejectsNullRequiredFields() {
        assertThatIllegalArgumentException().isThrownBy(() -> allocator.allocate(
                null, USD, List.of(BigInteger.ONE)));
        assertThatIllegalArgumentException().isThrownBy(() -> allocator.allocate(
                BigDecimal.ONE, null, List.of(BigInteger.ONE)));
        assertThatIllegalArgumentException().isThrownBy(() -> allocator.allocate(
                BigDecimal.ONE, USD, null));
    }

    @Test
    void rejectsEmptyWeights() {
        assertThatIllegalArgumentException().isThrownBy(() -> allocator.allocate(
                BigDecimal.ONE, USD, List.of()));
    }

    @Test
    void rejectsNullWeightEntries() {
        assertThatIllegalArgumentException().isThrownBy(() -> allocator.allocate(
                BigDecimal.ONE, USD, Arrays.asList(BigInteger.ONE, null)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "-10000000000000000000000000000000000000000"})
    void rejectsNonPositiveWeights(String invalidWeight) {
        assertThatIllegalArgumentException().isThrownBy(() -> allocator.allocate(
                BigDecimal.ONE, USD, List.of(BigInteger.ONE, new BigInteger(invalidWeight))));
    }

    @Test
    void protectsTheAllocationFromListMutation() {
        List<BigInteger> weights = new ArrayList<>(List.of(BigInteger.TWO, BigInteger.valueOf(3)));
        PaymentAllocation result = allocator.allocate(new BigDecimal("50.00"), USD, weights);

        weights.set(0, BigInteger.ONE);

        assertThat(result.weights()).containsExactly(BigInteger.TWO, BigInteger.valueOf(3));
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> result.weights().set(0, BigInteger.ONE));
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> result.allocations().set(0, BigDecimal.ZERO));
        assertAllocations(result, "20.00", "30.00");
    }

    private static void assertAllocations(PaymentAllocation result, String... expectedAmounts) {
        BigDecimal[] expected = Arrays.stream(expectedAmounts).map(BigDecimal::new).toArray(BigDecimal[]::new);
        assertThat(result.allocations()).usingElementComparator(BigDecimal::compareTo).containsExactly(expected);
        BigDecimal total = result.allocations().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        assertThat(total).isEqualByComparingTo(result.amount());
    }
}
