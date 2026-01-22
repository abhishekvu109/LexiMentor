package com.abhi.saarthi.cashflow.dto.dashboard;

import java.math.BigDecimal;

public record KpiOverview(
        Money totalBalance,
        BigDecimal balanceChangePercentage,     // 12.5  → positive = increase
        Money monthlySpending,
        BigDecimal spendingChangePercentage,    // 4.3
        int activeHouseholds,
        int activeHouseholdsChange,             // 0, +1, -2, etc.
        int budgetStatusPercentage,             // 72
        String budgetStatusLabel                // "On Track", "At Risk", "Over Budget"
) {
}
