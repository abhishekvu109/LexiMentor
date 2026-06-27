package com.abhi.asyncjobs.starter.autoconfigure;

import com.abhi.asyncjobs.api.JobClient;
import com.abhi.asyncjobs.starter.registration.AsyncJobRegistration;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.util.List;

public class AsyncJobsRegistrar implements ApplicationRunner {
    private final JobClient jobClient;
    private final List<AsyncJobRegistration> registrations;

    public AsyncJobsRegistrar(JobClient jobClient, List<AsyncJobRegistration> registrations) {
        this.jobClient = jobClient;
        this.registrations = registrations;
    }

    @Override
    public void run(ApplicationArguments args) {
        for (AsyncJobRegistration registration : registrations) {
            registration.register(jobClient);
        }
    }
}
