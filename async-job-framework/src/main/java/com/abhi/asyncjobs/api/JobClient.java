package com.abhi.asyncjobs.api;

import com.abhi.asyncjobs.model.JobRequest;
import com.abhi.asyncjobs.model.JobSnapshot;

import java.util.Collection;
import java.util.Optional;

public interface JobClient {
    <P, R> void register(JobDefinition<P, R> definition);

    String submit(JobRequest request);

    Optional<JobSnapshot> getJob(String jobId);

    Collection<JobSnapshot> listJobs();

    boolean cancel(String jobId);
}
