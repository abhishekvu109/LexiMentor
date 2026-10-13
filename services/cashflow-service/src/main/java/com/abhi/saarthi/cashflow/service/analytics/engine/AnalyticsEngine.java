package com.abhi.saarthi.cashflow.service.analytics.engine;

import com.abhi.saarthi.cashflow.dto.analytics.AnalyticsRequest;
import com.abhi.saarthi.cashflow.model.ExpenseAnalyticsContext;
import com.abhi.saarthi.cashflow.service.analytics.engine.strategy.AnalyticsStrategy;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class AnalyticsEngine {

    private final List<AnalyticsStrategy> strategies;
    public void execute(ExpenseAnalyticsContext context, AnalyticsRequest request) {
        strategies.stream()
                .filter(s -> request.requires(s.type()))
                .forEach(s -> s.compute(context));
    }
}