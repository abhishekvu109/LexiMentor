package com.abhi.saarthi.cashflow.service.analytics.pipeline;

import com.abhi.saarthi.cashflow.dto.analytics.AnalyticsRequest;
import com.abhi.saarthi.cashflow.model.ExpenseAnalyticsContext;

public interface AnalyticsStage {
    void process(ExpenseAnalyticsContext context, AnalyticsRequest request);
}