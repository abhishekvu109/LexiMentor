package com.abhi.saarthi.cashflow.service.impl;

import com.abhi.saarthi.cashflow.dto.analytics.AnalyticsBehaviorResponse;
import com.abhi.saarthi.cashflow.dto.analytics.AnalyticsCoreResponse;
import com.abhi.saarthi.cashflow.dto.analytics.AnalyticsDiagnosticResponse;
import com.abhi.saarthi.cashflow.dto.analytics.AnalyticsPlanningResponse;
import com.abhi.saarthi.cashflow.dto.analytics.AnalyticsRequest;
import com.abhi.saarthi.cashflow.dto.analytics.AnalyticsResult;
import com.abhi.saarthi.cashflow.entities.Expense;
import com.abhi.saarthi.cashflow.repository.ExpenseRepository;
import com.abhi.saarthi.cashflow.service.AnalyticsService;
import com.abhi.saarthi.cashflow.service.analytics.AnalyticsResultAssembler;
import com.abhi.saarthi.cashflow.service.analytics.orchestrator.AnalyticsOrchestratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AnalyticsServiceImpl implements AnalyticsService {
    private final AnalyticsOrchestratorService analyticsOrchestratorService;
    private final ExpenseRepository expenseRepository;
    private final AnalyticsResultAssembler analyticsResultAssembler;

    @Override
    public AnalyticsResult analyze(AnalyticsRequest request) {
        return analyzeInternal(request);
    }

    @Override
    public AnalyticsCoreResponse analyzeCore(AnalyticsRequest request) {
        AnalyticsResult result = analyzeInternal(request);
        return AnalyticsCoreResponse.builder()
                .summary(result.getSummary())
                .dailyTrend(result.getDailyTrend())
                .monthlyTrend(result.getMonthlyTrend())
                .categories(result.getCategories())
                .members(result.getMembers())
                .budgets(result.getBudgets())
                .comparison(result.getComparison())
                .forecast(result.getForecast())
                .anomalies(result.getAnomalies())
                .metrics(result.getMetrics())
                .insights(result.getInsights())
                .build();
    }

    @Override
    public AnalyticsBehaviorResponse analyzeBehavior(AnalyticsRequest request) {
        AnalyticsResult result = analyzeInternal(request);
        return AnalyticsBehaviorResponse.builder()
                .summary(result.getSummary())
                .behavior(result.getBehavior())
                .build();
    }

    @Override
    public AnalyticsDiagnosticResponse analyzeDiagnostic(AnalyticsRequest request) {
        AnalyticsResult result = analyzeInternal(request);
        return AnalyticsDiagnosticResponse.builder()
                .summary(result.getSummary())
                .diagnostic(result.getDiagnostic())
                .build();
    }

    @Override
    public AnalyticsPlanningResponse analyzePlanning(AnalyticsRequest request) {
        AnalyticsResult result = analyzeInternal(request);
        return AnalyticsPlanningResponse.builder()
                .summary(result.getSummary())
                .planning(result.getPlanning())
                .build();
    }

    private AnalyticsResult analyzeInternal(AnalyticsRequest request) {
        if (CollectionUtils.isEmpty(request.getAnalyticsTypes())) {
            request.setAnalyticsTypes(AnalyticsRequest.defaultAnalyticsTypes());
        }
        List<Expense> expenses = expenseRepository.findByHouseholdRefIdAndExpenseDateBetween(Long.parseLong(request.getHouseholdRefId()), request.getFrom(), request.getTo());
        return analyticsResultAssembler.assemble(
                analyticsOrchestratorService.analyze(expenses, request),
                request
        );
    }
}
