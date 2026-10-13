package com.abhi.asyncjobs.retry;

import com.abhi.asyncjobs.model.JobRequest;

public enum NoRetryPolicy implements RetryPolicy {
    INSTANCE;

    @Override
    public RetryDecision shouldRetry(int attempt, Throwable throwable, JobRequest request) {
        return RetryDecision.stop();
    }
}
