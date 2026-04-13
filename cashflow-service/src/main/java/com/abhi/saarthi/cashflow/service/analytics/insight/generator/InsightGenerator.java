package com.abhi.saarthi.cashflow.service.analytics.insight.generator;

import com.abhi.saarthi.cashflow.model.ExpenseAnalyticsContext;
import com.abhi.saarthi.cashflow.model.Insight;

import java.util.Optional;

public abstract class InsightGenerator {

    public Optional<Insight> generate(ExpenseAnalyticsContext context) {
        if (!condition(context)) return Optional.empty();
        return Optional.of(build(context));
    }

    protected abstract boolean condition(ExpenseAnalyticsContext context);
    protected abstract Insight build(ExpenseAnalyticsContext context);
}