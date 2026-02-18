package com.abhi.asyncjobs.event;

public enum JobEventType {
    SUBMITTED,
    STARTED,
    RETRY_SCHEDULED,
    SUCCEEDED,
    FAILED,
    CANCELLED
}
