package com.abhi.saarthi.cashflow.service.analytics.pipeline;

import com.abhi.saarthi.cashflow.dto.analytics.AnalyticsRequest;
import com.abhi.saarthi.cashflow.model.ExpenseAnalyticsContext;
import com.abhi.saarthi.cashflow.service.analytics.insight.InsightEngine;
import lombok.AllArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
@AllArgsConstructor
public class InsightStage implements AnalyticsStage {
    private final InsightEngine insightEngine;

    @Override
    public void process(ExpenseAnalyticsContext context, AnalyticsRequest request) {
        insightEngine.generate(context);
    }
}
