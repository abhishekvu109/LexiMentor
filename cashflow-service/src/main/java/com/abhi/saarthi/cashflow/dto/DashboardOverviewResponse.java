package com.abhi.saarthi.cashflow.dto;

import com.abhi.saarthi.cashflow.dto.dashboard.*;

import java.util.List;

public record DashboardOverviewResponse(
        KpiOverview kpi,
        SpendingTrend spendingTrend,
        List<HouseholdSummary> households,
        List<RecentTransaction> recentTransactions,
        DashboardMeta meta
) {
}




