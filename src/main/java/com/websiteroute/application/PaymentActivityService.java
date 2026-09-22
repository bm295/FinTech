package com.websiteroute.application;

import com.websiteroute.domain.Payment;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PaymentActivityService {
    private final List<Payment> payments = List.of(
            new Payment("payment-001", new BigDecimal("50.00"), "USD", "completed"));

    public List<Payment> getPaymentActivity() {
        return payments;
    }
}
