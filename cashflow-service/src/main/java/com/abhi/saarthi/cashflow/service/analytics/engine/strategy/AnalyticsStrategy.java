package com.abhi.saarthi.cashflow.service.analytics.engine.strategy;

import com.abhi.saarthi.cashflow.constants.AnalyticsType;
import com.abhi.saarthi.cashflow.model.ExpenseAnalyticsContext;

public interface AnalyticsStrategy {
    AnalyticsType type();
    void compute(ExpenseAnalyticsContext context);
}