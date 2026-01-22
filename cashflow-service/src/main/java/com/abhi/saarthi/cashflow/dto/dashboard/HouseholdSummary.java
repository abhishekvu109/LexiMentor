package com.abhi.saarthi.cashflow.dto.dashboard;

public record HouseholdSummary(
        String id,
        String name,
        int memberCount,
        Money balance
) {
}
