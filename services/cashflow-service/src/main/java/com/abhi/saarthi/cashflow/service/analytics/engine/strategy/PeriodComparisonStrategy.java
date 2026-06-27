package com.abhi.saarthi.cashflow.service.analytics.engine.strategy;

import com.abhi.saarthi.cashflow.constants.AnalyticsType;
import com.abhi.saarthi.cashflow.model.ExpenseAnalyticsContext;
import com.abhi.saarthi.cashflow.model.Metric;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PeriodComparisonStrategy implements AnalyticsStrategy {

    @Override
    public AnalyticsType type() {
        return AnalyticsType.PERIOD_COMPARISON;
    }

    @Override
    public void compute(ExpenseAnalyticsContext context) {

        if (context.comparison().isEmpty()) return;

        ExpenseAnalyticsContext prev = context.comparison().get();
        context.getMonthlyTotals().forEach((month, currentValue) -> {
            Double previousValue = prev.getMonthlyTotals().get(month);

            if (previousValue == null) return;

            double delta = currentValue - previousValue;
            double percentage =
                    (delta / previousValue) * 100;
            context.getMetrics().add(
                    new Metric(
                            "PERIOD_COMPARISON",
                            Map.of(
                                    "current", currentValue,
                                    "previous", previousValue,
                                    "change", delta,
                                    "percentage", percentage
                            )
                    )
            );
        });
    }
}