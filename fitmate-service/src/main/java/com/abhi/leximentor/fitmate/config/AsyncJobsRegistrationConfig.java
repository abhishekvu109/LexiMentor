package com.abhi.leximentor.fitmate.config;

import com.abhi.asyncjobs.api.JobDefinition;
import com.abhi.asyncjobs.retry.FixedDelayRetryPolicy;
import com.abhi.asyncjobs.starter.registration.AsyncJobRegistration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;

@Configuration
public class AsyncJobsRegistrationConfig {

    @Bean
    public AsyncJobRegistration fitmateAsyncJobRegistration() {
        return jobClient -> jobClient.register(
            JobDefinition.builder("fitmate.nutrition.recompute", Map.class, ctx -> Map.of(
                    "jobId", ctx.jobId(),
                    "app", "fitmate",
                    "status", "processed",
                    "payload", ctx.payload()
                ))
                .retryPolicy(new FixedDelayRetryPolicy(3, Duration.ofSeconds(2)))
                .build()
        );
    }
}
