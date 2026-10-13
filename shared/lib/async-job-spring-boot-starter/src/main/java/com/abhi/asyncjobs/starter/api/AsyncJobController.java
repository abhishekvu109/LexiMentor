package com.abhi.asyncjobs.starter.api;

import com.abhi.asyncjobs.api.JobClient;
import com.abhi.asyncjobs.model.JobRequest;
import com.abhi.asyncjobs.model.JobSnapshot;
import com.abhi.asyncjobs.model.JobStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("${async.jobs.api-base-path:/api/v1/async-jobs}")
public class AsyncJobController {
    private final JobClient jobClient;

    public AsyncJobController(JobClient jobClient) {
        this.jobClient = jobClient;
    }

    @PostMapping
    public ResponseEntity<SubmitJobResponse> submit(@RequestBody SubmitJobRequest request) {
        if (request.getJobType() == null || request.getJobType().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Map<String, String> metadata = request.getMetadata() == null
            ? new LinkedHashMap<>()
            : new LinkedHashMap<>(request.getMetadata());
        String idempotencyKey = metadata.get("idempotencyKey");

        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            Optional<JobSnapshot> activeExisting = findActiveByIdempotencyKey(request.getJobType(), idempotencyKey);
            if (activeExisting.isPresent()) {
                String existingJobId = activeExisting.get().getJobId();
                return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(new SubmitJobResponse(existingJobId, true, buildStatusUrl(existingJobId)));
            }
        }

        String jobId = jobClient.submit(JobRequest.of(request.getJobType(), request.getPayload(), metadata));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new SubmitJobResponse(jobId, false, buildStatusUrl(jobId)));
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<JobSnapshot> get(@PathVariable String jobId) {
        Optional<JobSnapshot> snapshot = jobClient.getJob(jobId);
        return snapshot.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public Collection<JobSnapshot> list() {
        return jobClient.listJobs();
    }

    @GetMapping("/search")
    public List<JobSnapshot> search(
        @RequestParam(required = false) String jobType,
        @RequestParam(required = false) String metadataKey,
        @RequestParam(required = false) String metadataValue,
        @RequestParam(required = false) String idempotencyKey,
        @RequestParam(defaultValue = "false") boolean activeOnly,
        @RequestParam(defaultValue = "20") int limit
    ) {
        return jobClient.listJobs().stream()
            .filter(job -> jobType == null || jobType.isBlank() || jobType.equals(job.getJobType()))
            .filter(job -> {
                if (idempotencyKey != null && !idempotencyKey.isBlank()) {
                    return idempotencyKey.equals(job.getMetadata().get("idempotencyKey"));
                }
                if (metadataKey != null && !metadataKey.isBlank()) {
                    return metadataValue != null && metadataValue.equals(job.getMetadata().get(metadataKey));
                }
                return true;
            })
            .filter(job -> !activeOnly || isActive(job.getStatus()))
            .sorted(Comparator.comparing(JobSnapshot::getCreatedAt).reversed())
            .limit(Math.max(1, limit))
            .toList();
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<Map<String, Object>> cancel(@PathVariable String jobId) {
        boolean cancelled = jobClient.cancel(jobId);
        if (!cancelled) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("jobId", jobId, "cancelled", false));
        }
        return ResponseEntity.ok(Map.of("jobId", jobId, "cancelled", true));
    }

    private Optional<JobSnapshot> findActiveByIdempotencyKey(String jobType, String idempotencyKey) {
        return jobClient.listJobs().stream()
            .filter(job -> jobType.equals(job.getJobType()))
            .filter(job -> idempotencyKey.equals(job.getMetadata().get("idempotencyKey")))
            .filter(job -> isActive(job.getStatus()))
            .max(Comparator.comparing(JobSnapshot::getCreatedAt));
    }

    private boolean isActive(JobStatus status) {
        return status == JobStatus.PENDING || status == JobStatus.RUNNING || status == JobStatus.RETRY_SCHEDULED;
    }

    private String buildStatusUrl(String jobId) {
        return "/api/v1/async-jobs/" + jobId;
    }
}
