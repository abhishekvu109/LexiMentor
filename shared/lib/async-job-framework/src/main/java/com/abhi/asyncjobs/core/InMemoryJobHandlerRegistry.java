package com.abhi.asyncjobs.core;

import com.abhi.asyncjobs.api.JobDefinition;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class InMemoryJobHandlerRegistry implements JobHandlerRegistry {
    private final ConcurrentMap<String, JobDefinition<?, ?>> definitions = new ConcurrentHashMap<>();

    @Override
    public <P, R> void register(JobDefinition<P, R> definition) {
        Objects.requireNonNull(definition, "definition must not be null");
        definitions.put(definition.jobType(), definition);
    }

    @Override
    public Optional<JobDefinition<?, ?>> find(String jobType) {
        return Optional.ofNullable(definitions.get(jobType));
    }
}
