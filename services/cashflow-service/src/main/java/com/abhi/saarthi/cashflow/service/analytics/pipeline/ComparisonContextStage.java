package com.abhi.saarthi.cashflow.service.analytics.pipeline;

import com.abhi.saarthi.cashflow.dto.analytics.AnalyticsRequest;
import com.abhi.saarthi.cashflow.entities.Expense;
import com.abhi.saarthi.cashflow.model.ExpenseAnalyticsContext;
import com.abhi.saarthi.cashflow.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(0) // runs BEFORE aggregation
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ComparisonContextStage implements AnalyticsStage {

    private final ExpenseRepository expenseRepository;

    @Override
    public void process(ExpenseAnalyticsContext context, AnalyticsRequest request) {

        if (!request.hasComparison()) {
            return;
        }

        List<Expense> previousExpenses =
                expenseRepository.findByHouseholdRefIdAndExpenseDateBetween(
                        Long.parseLong(request.getHouseholdRefId()),
                        request.getCompareFrom(),
                        request.getCompareTo()
                );

        ExpenseAnalyticsContext previousContext = new ExpenseAnalyticsContext();
        previousContext.getExpenses().addAll(previousExpenses);

        context.setComparisonContext(previousContext);
    }
}
