package com.abhi.asyncjobs.retry;

import com.abhi.asyncjobs.model.JobRequest;

import java.time.Duration;
import java.util.Objects;

public final class ExponentialBackoffRetryPolicy implements RetryPolicy {
    private final int maxAttempts;
    private final Duration initialDelay;
    private final double multiplier;
    private final Duration maxDelay;

    public ExponentialBackoffRetryPolicy(int maxAttempts, Duration initialDelay, double multiplier, Duration maxDelay) {
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("maxAttempts must be >= 1");
        }
        if (multiplier < 1.0d) {
            throw new IllegalArgumentException("multiplier must be >= 1.0");
        }
        this.maxAttempts = maxAttempts;
        this.initialDelay = Objects.requireNonNull(initialDelay, "initialDelay must not be null");
        this.multiplier = multiplier;
        this.maxDelay = Objects.requireNonNull(maxDelay, "maxDelay must not be null");
        if (initialDelay.isNegative() || maxDelay.isNegative()) {
            throw new IllegalArgumentException("delays must be non-negative");
        }
    }

    @Override
    public RetryDecision shouldRetry(int attempt, Throwable throwable, JobRequest request) {
        if (attempt >= maxAttempts) {
            return RetryDecision.stop();
        }

        double power = Math.pow(multiplier, Math.max(0, attempt - 1));
        long computedMillis = Math.round(initialDelay.toMillis() * power);
        long boundedMillis = Math.min(computedMillis, maxDelay.toMillis());
        return RetryDecision.retryAfter(Duration.ofMillis(Math.max(0L, boundedMillis)));
    }
}
