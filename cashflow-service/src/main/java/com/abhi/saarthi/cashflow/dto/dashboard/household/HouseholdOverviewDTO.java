package com.abhi.saarthi.cashflow.dto.dashboard.household;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

@Data
@Builder
@EqualsAndHashCode
@ToString
public class HouseholdOverviewDTO {
    private double availableBalance;
    private double totalSpent;
    private double budgetLeft;
    private Map<String,Double> spendingSplit;
    private Map<String, BudgetVsActual> budgetVsActual;
}
