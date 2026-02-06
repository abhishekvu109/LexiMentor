package com.abhi.leximentor.inventory.service.analytics.engine;

public interface AnalyticsStrategy<R> {
    AnalyticsType getType();

    R execute(AnalyticsRequest request);
}
