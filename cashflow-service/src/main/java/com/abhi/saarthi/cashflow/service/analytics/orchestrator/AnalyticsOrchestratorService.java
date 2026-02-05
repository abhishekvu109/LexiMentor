package com.abhi.saarthi.cashflow.service.analytics.orchestrator;

import com.abhi.saarthi.cashflow.dto.analytics.AnalyticsRequest;
import com.abhi.saarthi.cashflow.dto.analytics.AnalyticsResult;
import com.abhi.saarthi.cashflow.entities.Expense;
import com.abhi.saarthi.cashflow.model.ExpenseAnalyticsContext;
import com.abhi.saarthi.cashflow.service.analytics.pipeline.AnalyticsPipeline;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AnalyticsOrchestratorService {
    private final AnalyticsPipeline pipeline;

    public AnalyticsResult analyze(List<Expense> expenses, AnalyticsRequest request) {
        ExpenseAnalyticsContext context = new ExpenseAnalyticsContext();
        context.getExpenses().addAll(expenses);
        pipeline.execute(context, request);
        return AnalyticsResult.from(context);
    }
}