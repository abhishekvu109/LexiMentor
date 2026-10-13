package com.abhi.asyncjobs.retry;

import java.time.Duration;
import java.util.Objects;

public final class RetryDecision {
    private final boolean retry;
    private final Duration delay;

    private RetryDecision(boolean retry, Duration delay) {
        this.retry = retry;
        this.delay = delay == null ? Duration.ZERO : delay;
    }

    public static RetryDecision retryAfter(Duration delay) {
        Objects.requireNonNull(delay, "delay must not be null");
        if (delay.isNegative()) {
            throw new IllegalArgumentException("delay must be non-negative");
        }
        return new RetryDecision(true, delay);
    }

    public static RetryDecision stop() {
        return new RetryDecision(false, Duration.ZERO);
    }

    public boolean shouldRetry() {
        return retry;
    }

    public Duration delay() {
        return delay;
    }
}
