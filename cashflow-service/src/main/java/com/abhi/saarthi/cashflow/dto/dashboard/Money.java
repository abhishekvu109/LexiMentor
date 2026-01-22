package com.abhi.saarthi.cashflow.dto.dashboard;

import java.math.BigDecimal;

public record Money(
        BigDecimal amount,
        String currency           // "INR", "USD", etc. — ISO 4217 code
) {
    // Optional: convenience constructor / factory
    public static Money of(BigDecimal amount, String currency) {
        return new Money(amount, currency);
    }

    public static Money zero(String currency) {
        return new Money(BigDecimal.ZERO, currency);
    }
}