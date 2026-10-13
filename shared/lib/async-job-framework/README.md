# async-job-framework

Shared Java 17 Maven library for asynchronous job submission, tracking, retries, and cancellation.

## Why this library

- Reuse one async model across `fitmate`, `leximentor`, and `writewise`.
- Standardize lifecycle states (`PENDING`, `RUNNING`, `RETRY_SCHEDULED`, `SUCCEEDED`, `FAILED`, `CANCELLED`).
- Keep business services focused on domain logic instead of thread/retry plumbing.

## Design principles and patterns used

- `SOLID`
  - `SRP`: separate job execution, retry policy, storage, and handler registry.
  - `OCP`: new retry strategies, stores, listeners, and handlers can be added without modifying engine internals.
  - `DIP`: `AsyncJobEngine` depends on abstractions (`JobStore`, `JobHandlerRegistry`, `RetryPolicy`).
- `Strategy Pattern`: `RetryPolicy` (`NoRetryPolicy`, `FixedDelayRetryPolicy`, `ExponentialBackoffRetryPolicy`).
- `Factory/Registry Pattern`: `JobHandlerRegistry` resolves `JobDefinition` by `jobType`.
- `Observer Pattern`: `JobEventListener` receives lifecycle events.
- `Builder Pattern`: `AsyncJobEngine.builder()` and `JobDefinition.builder(...)` for explicit construction.

## Add dependency

Install locally:

```powershell
mvn -f async-job-framework/pom.xml clean install
```

Then in other services:

```xml
<dependency>
    <groupId>com.abhi.platform</groupId>
    <artifactId>async-job-framework</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Quick usage

```java
try (AsyncJobEngine engine = AsyncJobEngine.builder().build()) {
    engine.register(
        JobDefinition.builder("generate-report", ReportRequest.class, ctx -> {
                // business logic
                return "report-" + ctx.payload().userId();
            })
            .retryPolicy(new FixedDelayRetryPolicy(3, Duration.ofSeconds(2)))
            .build()
    );

    String jobId = engine.submit(JobRequest.of("generate-report", new ReportRequest("u-1")));
    Optional<JobSnapshot> snapshot = engine.getJob(jobId);
}
```

## Extension points

- Replace in-memory store by implementing `JobStore` for DB-backed tracking.
- Attach listeners (`addListener`) for logs, metrics, notifications, audit.
- Add custom retry strategies by implementing `RetryPolicy`.
