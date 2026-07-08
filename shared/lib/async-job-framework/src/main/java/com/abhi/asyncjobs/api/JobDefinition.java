package com.abhi.asyncjobs.api;

import com.abhi.asyncjobs.retry.NoRetryPolicy;
import com.abhi.asyncjobs.retry.RetryPolicy;

import java.util.Objects;

public final class JobDefinition<P, R> {
    private final String jobType;
    private final Class<P> payloadType;
    private final JobHandler<P, R> handler;
    private final RetryPolicy retryPolicy;

    private JobDefinition(Builder<P, R> builder) {
        this.jobType = Objects.requireNonNull(builder.jobType, "jobType must not be null").trim();
        if (this.jobType.isEmpty()) {
            throw new IllegalArgumentException("jobType must not be blank");
        }
        this.payloadType = Objects.requireNonNull(builder.payloadType, "payloadType must not be null");
        this.handler = Objects.requireNonNull(builder.handler, "handler must not be null");
        this.retryPolicy = builder.retryPolicy == null ? NoRetryPolicy.INSTANCE : builder.retryPolicy;
    }

    public static <P, R> Builder<P, R> builder(String jobType, Class<P> payloadType, JobHandler<P, R> handler) {
        return new Builder<>(jobType, payloadType, handler);
    }

    public String jobType() {
        return jobType;
    }

    public Class<P> payloadType() {
        return payloadType;
    }

    public JobHandler<P, R> handler() {
        return handler;
    }

    public RetryPolicy retryPolicy() {
        return retryPolicy;
    }

    public static final class Builder<P, R> {
        private final String jobType;
        private final Class<P> payloadType;
        private final JobHandler<P, R> handler;
        private RetryPolicy retryPolicy;

        private Builder(String jobType, Class<P> payloadType, JobHandler<P, R> handler) {
            this.jobType = jobType;
            this.payloadType = payloadType;
            this.handler = handler;
        }

        public Builder<P, R> retryPolicy(RetryPolicy retryPolicy) {
            this.retryPolicy = retryPolicy;
            return this;
        }

        public JobDefinition<P, R> build() {
            return new JobDefinition<>(this);
        }
    }
}
