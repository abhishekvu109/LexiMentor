package com.abhi.asyncjobs.starter.api;

public class SubmitJobResponse {
    private final String jobId;
    private final boolean deduplicated;
    private final String statusUrl;

    public SubmitJobResponse(String jobId) {
        this(jobId, false, null);
    }

    public SubmitJobResponse(String jobId, boolean deduplicated, String statusUrl) {
        this.jobId = jobId;
        this.deduplicated = deduplicated;
        this.statusUrl = statusUrl;
    }

    public String getJobId() {
        return jobId;
    }

    public boolean isDeduplicated() {
        return deduplicated;
    }

    public String getStatusUrl() {
        return statusUrl;
    }
}
