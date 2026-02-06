package com.abhi.leximentor.inventory.service.analytics.engine;

public class AnalyticsPipeline<C> {
    private final AnalyticsHandler<C> first;

    public AnalyticsPipeline(AnalyticsHandler<C> first) {
        this.first = first;
    }

    public void execute(C context) {
        if (first != null) {
            first.handle(context);
        }
    }
}
