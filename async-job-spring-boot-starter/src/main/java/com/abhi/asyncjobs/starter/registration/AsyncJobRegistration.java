package com.abhi.asyncjobs.starter.registration;

import com.abhi.asyncjobs.api.JobClient;

@FunctionalInterface
public interface AsyncJobRegistration {
    void register(JobClient jobClient);
}
