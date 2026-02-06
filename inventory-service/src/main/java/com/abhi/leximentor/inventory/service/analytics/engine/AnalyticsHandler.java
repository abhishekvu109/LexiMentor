package com.abhi.leximentor.inventory.service.analytics.engine;

public interface AnalyticsHandler<C> {
    void handle(C context);

    void setNext(AnalyticsHandler<C> next);
}
