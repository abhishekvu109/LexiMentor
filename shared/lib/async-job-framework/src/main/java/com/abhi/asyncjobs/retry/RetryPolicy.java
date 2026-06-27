package com.abhi.asyncjobs.retry;

import com.abhi.asyncjobs.model.JobRequest;

public interface RetryPolicy {
    RetryDecision shouldRetry(int attempt, Throwable throwable, JobRequest request);
}
