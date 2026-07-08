package com.abhi.saarthi.cashflow.service.analytics.pipeline;

import com.abhi.saarthi.cashflow.dto.analytics.AnalyticsRequest;
import com.abhi.saarthi.cashflow.model.ExpenseAnalyticsContext;
import com.abhi.saarthi.cashflow.service.analytics.engine.AnalyticsEngine;
import lombok.AllArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
@AllArgsConstructor
public class AnalyticsExecutionStage implements AnalyticsStage {

    private final AnalyticsEngine engine;
    @Override
    public void process(ExpenseAnalyticsContext context, AnalyticsRequest request) {
        engine.execute(context, request);
    }
}