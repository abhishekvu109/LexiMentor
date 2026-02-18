package com.abhi.asyncjobs.retry;

import com.abhi.asyncjobs.model.JobRequest;

import java.time.Duration;
import java.util.Objects;

public final class FixedDelayRetryPolicy implements RetryPolicy {
    private final int maxAttempts;
    private final Duration delay;

    public FixedDelayRetryPolicy(int maxAttempts, Duration delay) {
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("maxAttempts must be >= 1");
        }
        this.maxAttempts = maxAttempts;
        this.delay = Objects.requireNonNull(delay, "delay must not be null");
        if (delay.isNegative()) {
            throw new IllegalArgumentException("delay must not be negative");
        }
    }

    @Override
    public RetryDecision shouldRetry(int attempt, Throwable throwable, JobRequest request) {
        return attempt < maxAttempts ? RetryDecision.retryAfter(delay) : RetryDecision.stop();
    }
}
