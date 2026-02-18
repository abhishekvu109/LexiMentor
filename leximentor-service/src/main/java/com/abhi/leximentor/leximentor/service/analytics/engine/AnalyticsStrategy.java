package com.abhi.leximentor.leximentor.service.analytics.engine;

public interface AnalyticsStrategy<R> {
    AnalyticsType getType();

    R execute(AnalyticsRequest request);
}
