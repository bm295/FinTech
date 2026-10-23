package com.websiteroute.payment.adapter.in.web;

import com.websiteroute.payment.application.PaymentAllocationService;
import com.websiteroute.payment.domain.PaymentAllocation;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments/allocations")
public class PaymentAllocationController {
    private final PaymentAllocationService service;

    public PaymentAllocationController(PaymentAllocationService service) {
        this.service = service;
    }

    @PostMapping
    public AllocationResponse allocate(@RequestBody AllocationRequest request) {
        PaymentAllocation result = service.allocate(request.amount(), request.currency(), request.weights());
        return new AllocationResponse(
                result.amount(), result.currency().getCurrencyCode(), result.weights(), result.allocations());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail invalidAllocation(IllegalArgumentException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail invalidRequestBody(HttpMessageNotReadableException exception) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "A JSON object with amount, currency and numeric weights is required.");
    }

    // Decimal parsing allows exact validation instead of silently truncating fractional JSON weights.
    public record AllocationRequest(BigDecimal amount, String currency, List<BigDecimal> weights) {}

    public record AllocationResponse(
            BigDecimal amount, String currency, List<BigInteger> weights, List<BigDecimal> allocations) {}
}
