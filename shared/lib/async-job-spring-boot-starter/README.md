# async-job-spring-boot-starter

Spring Boot auto-configuration for `async-job-framework`.

## Features

- Auto-creates `AsyncJobEngine` and `JobClient`.
- JDBC-backed durable `JobStore` (default) or memory store.
- REST endpoints:
  - `POST /api/v1/async-jobs`
  - `GET /api/v1/async-jobs/{jobId}`
  - `GET /api/v1/async-jobs`
  - `DELETE /api/v1/async-jobs/{jobId}`
- Hook to register jobs at startup via `AsyncJobRegistration`.

## Properties

```yaml
async:
  jobs:
    store-type: jdbc # jdbc | memory
    table-name: async_jobs
    worker-threads: 8
    scheduler-threads: 1
    api-enabled: true
    api-base-path: /api/v1/async-jobs
```

## DB schema

Run `src/main/resources/async-jobs-schema.sql` in your service database.
