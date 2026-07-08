package com.abhi.asyncjobs.store;

import com.abhi.asyncjobs.model.JobSnapshot;

import java.util.Collection;
import java.util.Optional;

public interface JobStore {
    void save(JobState state);

    Optional<JobState> findState(String jobId);

    Optional<JobSnapshot> findSnapshot(String jobId);

    Collection<JobSnapshot> listSnapshots();
}
