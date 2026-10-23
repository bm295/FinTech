package com.websiteroute.payment.adapter.in.web;

import com.websiteroute.payment.application.PaymentActivityService;
import com.websiteroute.payment.domain.Payment;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentActivityController {
    private final PaymentActivityService service;

    public PaymentActivityController(PaymentActivityService service) {
        this.service = service;
    }

    @GetMapping
    public List<Payment> getPaymentActivity() {
        return service.getPaymentActivity();
    }
}
