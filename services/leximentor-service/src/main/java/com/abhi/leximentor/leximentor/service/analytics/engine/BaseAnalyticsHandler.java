package com.abhi.leximentor.leximentor.service.analytics.engine;

public abstract class BaseAnalyticsHandler<C> implements AnalyticsHandler<C> {
    private AnalyticsHandler<C> next;

    @Override
    public void setNext(AnalyticsHandler<C> next) {
        this.next = next;
    }

    protected void next(C context) {
        if (next != null) {
            next.handle(context);
        }
    }
}
