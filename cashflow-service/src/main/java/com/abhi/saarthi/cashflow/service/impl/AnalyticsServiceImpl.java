package com.abhi.saarthi.cashflow.service.impl;

import com.abhi.saarthi.cashflow.dto.analytics.AnalyticsRequest;
import com.abhi.saarthi.cashflow.dto.analytics.AnalyticsResult;
import com.abhi.saarthi.cashflow.entities.Expense;
import com.abhi.saarthi.cashflow.repository.ExpenseRepository;
import com.abhi.saarthi.cashflow.service.AnalyticsService;
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

    @Override
    public AnalyticsResult analyze(AnalyticsRequest request) {
        if (CollectionUtils.isEmpty(request.getAnalyticsTypes())) {
            request.setAnalyticsTypes(AnalyticsRequest.defaultAnalyticsTypes());
        }
        List<Expense> expenses = expenseRepository.findByHouseholdRefIdAndExpenseDateBetween(Long.parseLong(request.getHouseholdRefId()), request.getFrom(), request.getTo());
        return analyticsOrchestratorService.analyze(expenses, request);
    }
}
