package com.abhi.saarthi.cashflow.service.analytics.insight.generator;

import com.abhi.saarthi.cashflow.constants.Severity;
import com.abhi.saarthi.cashflow.model.ExpenseAnalyticsContext;
import com.abhi.saarthi.cashflow.model.Insight;
import org.springframework.stereotype.Component;

@Component
public class OverspendingInsightGenerator extends InsightGenerator {

    @Override
    protected boolean condition(ExpenseAnalyticsContext context) {
        return context.getMonthlyTotals().values().stream().anyMatch(v -> v > 10000);
    }

    @Override
    protected Insight build(ExpenseAnalyticsContext context) {
        return new Insight(
                "OVERSPENDING",
                "Your spending is higher than usual this month",
                Severity.HIGH
        );
    }
}