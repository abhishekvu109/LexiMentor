package com.abhi.saarthi.cashflow.dto.dashboard;

import java.math.BigDecimal;

public record DailySpendingPoint(
        String day,           // "Mon", "Tue", ... or full "2026-01-15"
        BigDecimal amount     // daily total spent
) {
}
