package com.abhi.asyncjobs.api;

import java.time.Instant;
import java.util.Map;

public final class JobExecutionContext<P> {
    private final String jobId;
    private final int attempt;
    private final P payload;
    private final Map<String, String> metadata;
    private final Instant requestedAt;

    public JobExecutionContext(String jobId, int attempt, P payload, Map<String, String> metadata, Instant requestedAt) {
        this.jobId = jobId;
        this.attempt = attempt;
        this.payload = payload;
        this.metadata = metadata;
        this.requestedAt = requestedAt;
    }

    public String jobId() {
        return jobId;
    }

    public int attempt() {
        return attempt;
    }

    public P payload() {
        return payload;
    }

    public Map<String, String> metadata() {
        return metadata;
    }

    public Instant requestedAt() {
        return requestedAt;
    }
}
