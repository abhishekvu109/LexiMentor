package com.abhi.saarthi.cashflow.dto.dashboard;

public record DashboardMeta(
        String currency,          // global fallback if not set per Money
        String period             // mainly for chart title / label
) {}