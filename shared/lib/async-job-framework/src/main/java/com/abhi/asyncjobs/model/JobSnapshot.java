package com.abhi.asyncjobs.model;

import java.time.Instant;
import java.util.Map;

public final class JobSnapshot {
    private final String jobId;
    private final String jobType;
    private final JobStatus status;
    private final int attempt;
    private final Object result;
    private final JobError error;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Instant startedAt;
    private final Instant completedAt;
    private final Map<String, String> metadata;

    public JobSnapshot(
        String jobId,
        String jobType,
        JobStatus status,
        int attempt,
        Object result,
        JobError error,
        Instant createdAt,
        Instant updatedAt,
        Instant startedAt,
        Instant completedAt,
        Map<String, String> metadata
    ) {
        this.jobId = jobId;
        this.jobType = jobType;
        this.status = status;
        this.attempt = attempt;
        this.result = result;
        this.error = error;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public String jobId() {
        return jobId;
    }

    public String getJobId() {
        return jobId;
    }

    public String jobType() {
        return jobType;
    }

    public String getJobType() {
        return jobType;
    }

    public JobStatus status() {
        return status;
    }

    public JobStatus getStatus() {
        return status;
    }

    public int attempt() {
        return attempt;
    }

    public int getAttempt() {
        return attempt;
    }

    public Object result() {
        return result;
    }

    public Object getResult() {
        return result;
    }

    public JobError error() {
        return error;
    }

    public JobError getError() {
        return error;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant startedAt() {
        return startedAt;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant completedAt() {
        return completedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public Map<String, String> metadata() {
        return metadata;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }
}
