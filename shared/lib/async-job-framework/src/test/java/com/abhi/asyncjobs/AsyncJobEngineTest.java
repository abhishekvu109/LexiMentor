package com.abhi.asyncjobs;

import com.abhi.asyncjobs.api.JobDefinition;
import com.abhi.asyncjobs.core.AsyncJobEngine;
import com.abhi.asyncjobs.model.JobRequest;
import com.abhi.asyncjobs.model.JobSnapshot;
import com.abhi.asyncjobs.model.JobStatus;
import com.abhi.asyncjobs.retry.FixedDelayRetryPolicy;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AsyncJobEngineTest {

    @Test
    void shouldExecuteAndTrackSuccessfulJob() throws Exception {
        try (AsyncJobEngine engine = AsyncJobEngine.builder().build()) {
            engine.register(JobDefinition.builder("uppercase", String.class, ctx -> ctx.payload().toUpperCase()).build());
            String jobId = engine.submit(JobRequest.of("uppercase", "fitmate"));

            JobSnapshot snapshot = waitForTerminal(engine, jobId, 3000);
            assertEquals(JobStatus.SUCCEEDED, snapshot.status());
            assertEquals("FITMATE", snapshot.result());
        }
    }

    @Test
    void shouldRetryAndSucceed() throws Exception {
        AtomicInteger invocationCounter = new AtomicInteger();

        try (AsyncJobEngine engine = AsyncJobEngine.builder().build()) {
            engine.register(JobDefinition.builder("flaky", String.class, ctx -> {
                    if (invocationCounter.incrementAndGet() < 2) {
                        throw new IllegalStateException("first call fails");
                    }
                    return "ok-" + ctx.payload();
                })
                .retryPolicy(new FixedDelayRetryPolicy(3, Duration.ofMillis(100)))
                .build());

            String jobId = engine.submit(JobRequest.of("flaky", "job"));
            JobSnapshot snapshot = waitForTerminal(engine, jobId, 4000);

            assertEquals(JobStatus.SUCCEEDED, snapshot.status());
            assertEquals(2, snapshot.attempt());
            assertEquals("ok-job", snapshot.result());
        }
    }

    @Test
    void shouldFailAfterRetriesExhausted() throws Exception {
        try (AsyncJobEngine engine = AsyncJobEngine.builder().build()) {
            engine.register(JobDefinition.builder("always-fail", String.class, ctx -> {
                    throw new IllegalArgumentException("nope");
                })
                .retryPolicy(new FixedDelayRetryPolicy(2, Duration.ofMillis(50)))
                .build());

            String jobId = engine.submit(JobRequest.of("always-fail", "x"));
            JobSnapshot snapshot = waitForTerminal(engine, jobId, 4000);

            assertEquals(JobStatus.FAILED, snapshot.status());
            assertEquals(2, snapshot.attempt());
            assertEquals("java.lang.IllegalArgumentException", snapshot.error().type());
        }
    }

    @Test
    void shouldCancelQueuedJob() throws Exception {
        CountDownLatch blocker = new CountDownLatch(1);
        var workerPool = Executors.newSingleThreadExecutor();
        var scheduler = Executors.newSingleThreadScheduledExecutor();

        try (AsyncJobEngine engine = AsyncJobEngine.builder()
            .workerPool(workerPool)
            .scheduler(scheduler)
            .build()) {

            engine.register(JobDefinition.builder("blocker", String.class, ctx -> {
                blocker.await(2, TimeUnit.SECONDS);
                return "done";
            }).build());

            engine.register(JobDefinition.builder("cancel-me", String.class, ctx -> "will-not-run").build());

            String firstJob = engine.submit(JobRequest.of("blocker", "a"));
            String secondJob = engine.submit(JobRequest.of("cancel-me", "b"));
            boolean cancelled = engine.cancel(secondJob);
            blocker.countDown();

            JobSnapshot cancelledSnapshot = waitForTerminal(engine, secondJob, 4000);
            assertTrue(cancelled);
            assertEquals(JobStatus.CANCELLED, cancelledSnapshot.status());

            JobSnapshot firstSnapshot = waitForTerminal(engine, firstJob, 4000);
            assertEquals(JobStatus.SUCCEEDED, firstSnapshot.status());
        } finally {
            workerPool.shutdownNow();
            scheduler.shutdownNow();
        }
    }

    private JobSnapshot waitForTerminal(AsyncJobEngine engine, String jobId, long timeoutMillis) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMillis;
        while (System.currentTimeMillis() < deadline) {
            Optional<JobSnapshot> snapshotOpt = engine.getJob(jobId);
            if (snapshotOpt.isPresent()) {
                JobSnapshot snapshot = snapshotOpt.get();
                if (snapshot.status() == JobStatus.SUCCEEDED
                    || snapshot.status() == JobStatus.FAILED
                    || snapshot.status() == JobStatus.CANCELLED) {
                    return snapshot;
                }
            }
            Thread.sleep(25);
        }
        throw new AssertionError("Job did not reach terminal state within timeout. jobId=" + jobId);
    }
}
