package com.abhi.saarthi.cashflow.dto.dashboard;

public record BudgetStatus(double budgetStatusPercentage, String budgetStatusLabel) {
    public static BudgetStatus of(double budgetStatusPercentage) {
        if (budgetStatusPercentage < 90)
            return new BudgetStatus(budgetStatusPercentage, "On Track");
        else if (budgetStatusPercentage >= 90 && budgetStatusPercentage <= 100)
            return new BudgetStatus(budgetStatusPercentage, "At Risk");
        else
            return new BudgetStatus(budgetStatusPercentage, "Over budget");
    }
}
