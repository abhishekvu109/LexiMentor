package com.abhi.saarthi.cashflow.service.analytics.pipeline;

import com.abhi.saarthi.cashflow.dto.analytics.AnalyticsRequest;
import com.abhi.saarthi.cashflow.model.ExpenseAnalyticsContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.YearMonth;

@Component
@Order(1)
public class AggregationStage implements AnalyticsStage {

    @Override
    public void process(ExpenseAnalyticsContext context, AnalyticsRequest request) {
        context.getExpenses().forEach(e -> {
            context.getCategoryTotals()
                    .merge(e.getCategory().getName(), e.getAmount(), Double::sum);

            context.getDailyTotals()
                    .merge(e.getExpenseDate(), e.getAmount(), Double::sum);

            context.getMonthlyTotals()
                    .merge(YearMonth.from(e.getExpenseDate()), e.getAmount(), Double::sum);

            context.getMemberTotals()
                    .merge(e.getOwner(), e.getAmount(), Double::sum);
        });

        if (request.hasComparison() && context.comparison().isPresent()) {
            context.comparison().get().getExpenses().forEach(e -> {
                context.comparison().get().getCategoryTotals()
                        .merge(e.getCategory().getName(), e.getAmount(), Double::sum);

                context.comparison().get().getDailyTotals()
                        .merge(e.getExpenseDate(), e.getAmount(), Double::sum);

                context.comparison().get().getMonthlyTotals()
                        .merge(YearMonth.from(e.getExpenseDate()), e.getAmount(), Double::sum);

                context.comparison().get().getMemberTotals()
                        .merge(e.getOwner(), e.getAmount(), Double::sum);
            });
        }
    }
}