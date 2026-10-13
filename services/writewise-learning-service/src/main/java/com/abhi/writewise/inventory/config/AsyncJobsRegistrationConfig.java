package com.abhi.writewise.inventory.config;

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
    public AsyncJobRegistration writewiseAsyncJobRegistration() {
        return jobClient -> jobClient.register(
            JobDefinition.builder("writewise.response.evaluate", Map.class, ctx -> Map.of(
                    "jobId", ctx.jobId(),
                    "app", "writewise",
                    "status", "processed",
                    "attempt", ctx.attempt(),
                    "payload", ctx.payload()
                ))
                .retryPolicy(new FixedDelayRetryPolicy(3, Duration.ofSeconds(1)))
                .build()
        );
    }
}
