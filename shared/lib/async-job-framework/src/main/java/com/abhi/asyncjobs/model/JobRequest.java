package com.abhi.asyncjobs.model;

import java.util.Map;
import java.util.Objects;

public final class JobRequest {
    private final String jobType;
    private final Object payload;
    private final Map<String, String> metadata;

    private JobRequest(String jobType, Object payload, Map<String, String> metadata) {
        this.jobType = Objects.requireNonNull(jobType, "jobType must not be null").trim();
        if (this.jobType.isEmpty()) {
            throw new IllegalArgumentException("jobType must not be blank");
        }
        this.payload = payload;
        this.metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static JobRequest of(String jobType, Object payload) {
        return new JobRequest(jobType, payload, Map.of());
    }

    public static JobRequest of(String jobType, Object payload, Map<String, String> metadata) {
        return new JobRequest(jobType, payload, metadata);
    }

    public String jobType() {
        return jobType;
    }

    public Object payload() {
        return payload;
    }

    public Map<String, String> metadata() {
        return metadata;
    }
}
