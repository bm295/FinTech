package com.websiteroute.domain;

import java.math.BigDecimal;

public record Payment(String id, BigDecimal amount, String currency, String status) {
}
