package com.abhi.asyncjobs.store;

import com.abhi.asyncjobs.model.JobSnapshot;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class InMemoryJobStore implements JobStore {
    private final ConcurrentMap<String, JobState> storage = new ConcurrentHashMap<>();

    @Override
    public void save(JobState state) {
        storage.put(state.jobId(), state);
    }

    @Override
    public Optional<JobState> findState(String jobId) {
        return Optional.ofNullable(storage.get(jobId));
    }

    @Override
    public Optional<JobSnapshot> findSnapshot(String jobId) {
        return findState(jobId).map(JobState::snapshot);
    }

    @Override
    public Collection<JobSnapshot> listSnapshots() {
        return storage.values().stream()
            .map(JobState::snapshot)
            .toList();
    }
}
