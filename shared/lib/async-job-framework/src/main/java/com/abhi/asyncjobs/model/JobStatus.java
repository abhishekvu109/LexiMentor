package com.abhi.asyncjobs.model;

public enum JobStatus {
    PENDING,
    RUNNING,
    RETRY_SCHEDULED,
    SUCCEEDED,
    FAILED,
    CANCELLED
}
