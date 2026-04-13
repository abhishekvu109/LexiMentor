package com.abhi.leximentor.leximentor.service.analytics.engine;

public interface AnalyticsHandler<C> {
    void handle(C context);

    void setNext(AnalyticsHandler<C> next);
}
