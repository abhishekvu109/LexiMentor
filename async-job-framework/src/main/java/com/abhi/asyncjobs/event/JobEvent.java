package com.abhi.asyncjobs.event;

import com.abhi.asyncjobs.model.JobSnapshot;

import java.time.Instant;
import java.util.Objects;

public final class JobEvent {
    private final JobEventType type;
    private final JobSnapshot snapshot;
    private final Instant occurredAt;

    public JobEvent(JobEventType type, JobSnapshot snapshot) {
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.snapshot = Objects.requireNonNull(snapshot, "snapshot must not be null");
        this.occurredAt = Instant.now();
    }

    public JobEventType type() {
        return type;
    }

    public JobSnapshot snapshot() {
        return snapshot;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
