package com.abhi.asyncjobs.core;

import com.abhi.asyncjobs.api.JobClient;
import com.abhi.asyncjobs.api.JobDefinition;
import com.abhi.asyncjobs.api.JobExecutionContext;
import com.abhi.asyncjobs.event.JobEvent;
import com.abhi.asyncjobs.event.JobEventListener;
import com.abhi.asyncjobs.event.JobEventType;
import com.abhi.asyncjobs.model.JobRequest;
import com.abhi.asyncjobs.model.JobSnapshot;
import com.abhi.asyncjobs.model.JobStatus;
import com.abhi.asyncjobs.retry.RetryDecision;
import com.abhi.asyncjobs.store.InMemoryJobStore;
import com.abhi.asyncjobs.store.JobState;
import com.abhi.asyncjobs.store.JobStore;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public final class AsyncJobEngine implements JobClient, AutoCloseable {
    private final JobStore jobStore;
    private final JobHandlerRegistry handlerRegistry;
    private final ExecutorService workerPool;
    private final ScheduledExecutorService scheduler;
    private final Supplier<String> jobIdGenerator;
    private final PayloadCoercionStrategy payloadCoercionStrategy;
    private final List<JobEventListener> listeners;
    private final ConcurrentMap<String, Future<?>> inFlight = new ConcurrentHashMap<>();

    private AsyncJobEngine(Builder builder) {
        this.jobStore = builder.jobStore;
        this.handlerRegistry = builder.handlerRegistry;
        this.workerPool = builder.workerPool;
        this.scheduler = builder.scheduler;
        this.jobIdGenerator = builder.jobIdGenerator;
        this.payloadCoercionStrategy = builder.payloadCoercionStrategy;
        this.listeners = builder.listeners;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public <P, R> void register(JobDefinition<P, R> definition) {
        handlerRegistry.register(definition);
    }

    @Override
    public String submit(JobRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        JobDefinition<?, ?> definition = handlerRegistry.find(request.jobType())
            .orElseThrow(() -> new IllegalArgumentException("No job definition registered for type: " + request.jobType()));
        Object payload = payloadCoercionStrategy.coerce(request.payload(), definition.payloadType());
        validatePayloadType(definition, payload);
        JobRequest resolvedRequest = JobRequest.of(request.jobType(), payload, request.metadata());

        String jobId = jobIdGenerator.get();
        JobState state = new JobState(jobId, resolvedRequest);
        jobStore.save(state);
        publish(JobEventType.SUBMITTED, state.snapshot());
        dispatch(jobId);
        return jobId;
    }

    @Override
    public Optional<JobSnapshot> getJob(String jobId) {
        return jobStore.findSnapshot(jobId);
    }

    @Override
    public Collection<JobSnapshot> listJobs() {
        return jobStore.listSnapshots();
    }

    @Override
    public boolean cancel(String jobId) {
        Optional<JobState> optionalState = jobStore.findState(jobId);
        if (optionalState.isEmpty()) {
            return false;
        }

        JobState state = optionalState.get();
        synchronized (state) {
            JobStatus status = state.status();
            if (status == JobStatus.SUCCEEDED || status == JobStatus.FAILED || status == JobStatus.CANCELLED) {
                return false;
            }
            state.markCancelled();
            jobStore.save(state);
        }

        Future<?> future = inFlight.remove(jobId);
        if (future != null) {
            future.cancel(true);
        }
        publish(JobEventType.CANCELLED, state.snapshot());
        return true;
    }

    private void dispatch(String jobId) {
        Future<?> future = workerPool.submit(() -> execute(jobId));
        inFlight.put(jobId, future);
    }

    private void scheduleRetry(String jobId, long delayMillis) {
        scheduler.schedule(() -> {
            Future<?> future = workerPool.submit(() -> execute(jobId));
            inFlight.put(jobId, future);
        }, delayMillis, TimeUnit.MILLISECONDS);
    }

    private void execute(String jobId) {
        Optional<JobState> optionalState = jobStore.findState(jobId);
        if (optionalState.isEmpty()) {
            return;
        }

        JobState state = optionalState.get();
        int attempt;
        synchronized (state) {
            JobStatus status = state.status();
            if (status == JobStatus.CANCELLED || status == JobStatus.SUCCEEDED || status == JobStatus.FAILED) {
                return;
            }
            attempt = state.markRunningAndGetAttempt();
            jobStore.save(state);
        }

        publish(JobEventType.STARTED, state.snapshot());

        JobDefinition<?, ?> untypedDefinition = handlerRegistry.find(state.request().jobType())
            .orElseThrow(() -> new IllegalStateException("Missing job definition during execution"));

        try {
            Object result = executeTyped(untypedDefinition, state, attempt);
            synchronized (state) {
                if (state.status() == JobStatus.CANCELLED) {
                    return;
                }
                state.markSucceeded(result);
                jobStore.save(state);
            }
            publish(JobEventType.SUCCEEDED, state.snapshot());
        } catch (Throwable throwable) {
            RetryDecision retryDecision = untypedDefinition.retryPolicy()
                .shouldRetry(attempt, throwable, state.request());

            synchronized (state) {
                if (state.status() == JobStatus.CANCELLED) {
                    return;
                }
                if (retryDecision.shouldRetry()) {
                    state.markRetryScheduled(throwable);
                } else {
                    state.markFailed(throwable);
                }
                jobStore.save(state);
            }

            if (retryDecision.shouldRetry()) {
                publish(JobEventType.RETRY_SCHEDULED, state.snapshot());
                scheduleRetry(jobId, retryDecision.delay().toMillis());
            } else {
                publish(JobEventType.FAILED, state.snapshot());
            }
        } finally {
            inFlight.remove(jobId);
        }
    }

    @SuppressWarnings("unchecked")
    private <P, R> R executeTyped(JobDefinition<?, ?> definition, JobState state, int attempt) throws Exception {
        JobDefinition<P, R> typedDefinition = (JobDefinition<P, R>) definition;
        P payload = typedDefinition.payloadType().cast(state.request().payload());
        JobExecutionContext<P> context = new JobExecutionContext<>(
            state.jobId(),
            attempt,
            payload,
            state.request().metadata(),
            state.snapshot().createdAt()
        );
        return typedDefinition.handler().handle(context);
    }

    private void validatePayloadType(JobDefinition<?, ?> definition, Object payload) {
        if (payload == null) {
            return;
        }
        if (!definition.payloadType().isAssignableFrom(payload.getClass())) {
            throw new IllegalArgumentException(
                "Invalid payload type for " + definition.jobType() + ". Expected " + definition.payloadType().getName()
                    + ", got " + payload.getClass().getName()
            );
        }
    }

    private void publish(JobEventType eventType, JobSnapshot snapshot) {
        JobEvent event = new JobEvent(eventType, snapshot);
        for (JobEventListener listener : listeners) {
            listener.onEvent(event);
        }
    }

    @Override
    public void close() {
        workerPool.shutdown();
        scheduler.shutdown();
    }

    public static final class Builder {
        private JobStore jobStore = new InMemoryJobStore();
        private JobHandlerRegistry handlerRegistry = new InMemoryJobHandlerRegistry();
        private ExecutorService workerPool = Executors.newFixedThreadPool(Math.max(2, Runtime.getRuntime().availableProcessors()));
        private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        private Supplier<String> jobIdGenerator = () -> UUID.randomUUID().toString();
        private PayloadCoercionStrategy payloadCoercionStrategy = StrictPayloadCoercionStrategy.INSTANCE;
        private final List<JobEventListener> listeners = new CopyOnWriteArrayList<>();

        public Builder jobStore(JobStore jobStore) {
            this.jobStore = Objects.requireNonNull(jobStore, "jobStore must not be null");
            return this;
        }

        public Builder handlerRegistry(JobHandlerRegistry handlerRegistry) {
            this.handlerRegistry = Objects.requireNonNull(handlerRegistry, "handlerRegistry must not be null");
            return this;
        }

        public Builder workerPool(ExecutorService workerPool) {
            this.workerPool = Objects.requireNonNull(workerPool, "workerPool must not be null");
            return this;
        }

        public Builder scheduler(ScheduledExecutorService scheduler) {
            this.scheduler = Objects.requireNonNull(scheduler, "scheduler must not be null");
            return this;
        }

        public Builder jobIdGenerator(Supplier<String> jobIdGenerator) {
            this.jobIdGenerator = Objects.requireNonNull(jobIdGenerator, "jobIdGenerator must not be null");
            return this;
        }

        public Builder payloadCoercionStrategy(PayloadCoercionStrategy payloadCoercionStrategy) {
            this.payloadCoercionStrategy = Objects.requireNonNull(payloadCoercionStrategy, "payloadCoercionStrategy must not be null");
            return this;
        }

        public Builder addListener(JobEventListener listener) {
            this.listeners.add(Objects.requireNonNull(listener, "listener must not be null"));
            return this;
        }

        public AsyncJobEngine build() {
            return new AsyncJobEngine(this);
        }
    }
}
