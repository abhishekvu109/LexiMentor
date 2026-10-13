package com.abhi.asyncjobs.store;

import com.abhi.asyncjobs.model.JobError;
import com.abhi.asyncjobs.model.JobRequest;
import com.abhi.asyncjobs.model.JobSnapshot;
import com.abhi.asyncjobs.model.JobStatus;

import java.time.Instant;
import java.util.Objects;

public final class JobState {
    private final String jobId;
    private final JobRequest request;
    private final Instant createdAt;

    private JobStatus status;
    private int attempt;
    private Object result;
    private JobError error;
    private Instant updatedAt;
    private Instant startedAt;
    private Instant completedAt;

    public JobState(String jobId, JobRequest request) {
        this.jobId = Objects.requireNonNull(jobId, "jobId must not be null");
        this.request = Objects.requireNonNull(request, "request must not be null");
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.status = JobStatus.PENDING;
    }

    public synchronized JobStatus status() {
        return status;
    }

    public synchronized int attempt() {
        return attempt;
    }

    public synchronized int markRunningAndGetAttempt() {
        attempt++;
        status = JobStatus.RUNNING;
        error = null;
        if (startedAt == null) {
            startedAt = Instant.now();
        }
        updatedAt = Instant.now();
        return attempt;
    }

    public synchronized void markRetryScheduled(Throwable throwable) {
        status = JobStatus.RETRY_SCHEDULED;
        error = JobError.from(throwable);
        updatedAt = Instant.now();
    }

    public synchronized void markSucceeded(Object finalResult) {
        status = JobStatus.SUCCEEDED;
        result = finalResult;
        error = null;
        completedAt = Instant.now();
        updatedAt = completedAt;
    }

    public synchronized void markFailed(Throwable throwable) {
        status = JobStatus.FAILED;
        error = JobError.from(throwable);
        completedAt = Instant.now();
        updatedAt = completedAt;
    }

    public synchronized void markCancelled() {
        status = JobStatus.CANCELLED;
        completedAt = Instant.now();
        updatedAt = completedAt;
    }

    public synchronized JobSnapshot snapshot() {
        return new JobSnapshot(
            jobId,
            request.jobType(),
            status,
            attempt,
            result,
            error,
            createdAt,
            updatedAt,
            startedAt,
            completedAt,
            request.metadata()
        );
    }

    public String jobId() {
        return jobId;
    }

    public JobRequest request() {
        return request;
    }
}
