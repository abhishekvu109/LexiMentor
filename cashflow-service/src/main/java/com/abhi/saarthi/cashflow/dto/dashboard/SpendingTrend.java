package com.abhi.saarthi.cashflow.dto.dashboard;

import java.util.List;

public record SpendingTrend(
        List<DailySpendingPoint> points,
        String periodLabel                      // "Last 7 Days", "This Month", etc.
) {
}
