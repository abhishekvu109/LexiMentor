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
            String category = e.getCategory() != null ? e.getCategory().getName() : "UNCATEGORIZED";
            String owner = e.getOwner() != null ? e.getOwner() : "UNKNOWN";

            context.setTotalAmount(context.getTotalAmount() + e.getAmount());
            context.setTransactionCount(context.getTransactionCount() + 1);

            context.getCategoryTotals()
                    .merge(category, e.getAmount(), Double::sum);
            context.getCategoryCounts()
                    .merge(category, 1L, Long::sum);

            context.getDailyTotals()
                    .merge(e.getExpenseDate(), e.getAmount(), Double::sum);

            context.getMonthlyTotals()
                    .merge(YearMonth.from(e.getExpenseDate()), e.getAmount(), Double::sum);

            context.getMemberTotals()
                    .merge(owner, e.getAmount(), Double::sum);
            context.getMemberCounts()
                    .merge(owner, 1L, Long::sum);
        });

        if (request.hasComparison() && context.comparison().isPresent()) {
            context.comparison().get().getExpenses().forEach(e -> {
                String category = e.getCategory() != null ? e.getCategory().getName() : "UNCATEGORIZED";
                String owner = e.getOwner() != null ? e.getOwner() : "UNKNOWN";

                context.comparison().get().setTotalAmount(context.comparison().get().getTotalAmount() + e.getAmount());
                context.comparison().get().setTransactionCount(context.comparison().get().getTransactionCount() + 1);

                context.comparison().get().getCategoryTotals()
                        .merge(category, e.getAmount(), Double::sum);
                context.comparison().get().getCategoryCounts()
                        .merge(category, 1L, Long::sum);

                context.comparison().get().getDailyTotals()
                        .merge(e.getExpenseDate(), e.getAmount(), Double::sum);

                context.comparison().get().getMonthlyTotals()
                        .merge(YearMonth.from(e.getExpenseDate()), e.getAmount(), Double::sum);

                context.comparison().get().getMemberTotals()
                        .merge(owner, e.getAmount(), Double::sum);
                context.comparison().get().getMemberCounts()
                        .merge(owner, 1L, Long::sum);
            });
        }
    }
}
