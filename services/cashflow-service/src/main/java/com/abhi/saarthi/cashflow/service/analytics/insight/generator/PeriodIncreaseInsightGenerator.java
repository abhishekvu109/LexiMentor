package com.abhi.saarthi.cashflow.service.analytics.insight.generator;

import com.abhi.saarthi.cashflow.constants.Severity;
import com.abhi.saarthi.cashflow.model.ExpenseAnalyticsContext;
import com.abhi.saarthi.cashflow.model.Insight;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PeriodIncreaseInsightGenerator extends InsightGenerator {

    @Override
    protected boolean condition(ExpenseAnalyticsContext context) {
        return context.getMetrics().stream()
                .anyMatch(m ->
                        m.getName().startsWith("PERIOD_COMPARISON") &&
                                ((Map<?, ?>) m.getValue()).get("percentage") instanceof Double p &&
                                p > 20
                );
    }

    @Override
    protected Insight build(ExpenseAnalyticsContext context) {
        return new Insight(
                "PERIOD_INCREASE",
                "Your spending increased significantly compared to the previous period",
                Severity.MEDIUM
        );
    }
}