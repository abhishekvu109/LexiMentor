package com.abhi.asyncjobs.core;

import com.abhi.asyncjobs.api.JobDefinition;

import java.util.Optional;

public interface JobHandlerRegistry {
    <P, R> void register(JobDefinition<P, R> definition);

    Optional<JobDefinition<?, ?>> find(String jobType);
}
