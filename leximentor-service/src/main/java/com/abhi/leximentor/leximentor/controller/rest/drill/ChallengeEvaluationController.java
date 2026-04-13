package com.abhi.leximentor.leximentor.controller.rest.drill;

import com.abhi.asyncjobs.api.JobClient;
import com.abhi.asyncjobs.model.JobSnapshot;
import com.abhi.asyncjobs.model.JobStatus;
import com.abhi.leximentor.leximentor.constants.ApplicationConstants;
import com.abhi.leximentor.leximentor.dto.drill.ChallengeEvaluationJobPayload;
import com.abhi.leximentor.leximentor.dto.drill.ChallengeReportResponseDTO;
import com.abhi.leximentor.leximentor.entities.drill.Challenge;
import com.abhi.leximentor.leximentor.model.rest.ResponseEntityBuilder;
import com.abhi.leximentor.leximentor.model.rest.RestApiResponse;
import com.abhi.leximentor.leximentor.repository.drill.ChallengeRepository;
import com.abhi.leximentor.leximentor.service.drill.ChallengeEvaluationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping({"/api/v1/leximentor/challenges", "/api/leximentor/drill/metadata/challenges/challenge"})
public class ChallengeEvaluationController {
    private static final String REQUEST_FAILURE_CODE = "001";
    private static final String ASYNC_URL = "/api/leximentor/async-jobs";

    private final ChallengeRepository challengeRepository;
    private final ChallengeEvaluationService challengeEvaluationService;
    private final JobClient jobClient;

    @PostMapping(value = {"/{challengeId}/evaluations", "/{challengeId}/evaluate"}, consumes = ApplicationConstants.MediaType.APPLICATION_JSON, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> evaluateChallenge(@PathVariable String challengeId, @RequestParam String evaluator) {
        log.info("Received a request for the evaluation of the challenge {} using evaluator {}", challengeId, evaluator);
        Challenge challenge = challengeRepository.findByKey(challengeId);
        if (challenge == null) {
            return ResponseEntityBuilder.getBuilder(HttpStatus.NOT_FOUND)
                    .errorResponse(REQUEST_FAILURE_CODE, "Challenge not found.");
        }
        log.info("Successfully fetched the drill challenge objects using the challenge id {},{}", challengeId, challenge);
        String idempotencyKey = "leximentor.drill.challenge.evaluate:" + challengeId;
        Optional<JobSnapshot> existingActive = findActiveByIdempotencyKey("leximentor.drill.challenge.evaluate", idempotencyKey);

        String jobId;
        boolean deduplicated;
        if (existingActive.isPresent()) {
            jobId = existingActive.get().getJobId();
            deduplicated = true;
        } else {
            jobId = jobClient.submit(com.abhi.asyncjobs.model.JobRequest.of(
                    "leximentor.drill.challenge.evaluate",
                    new ChallengeEvaluationJobPayload(challengeId, evaluator),
                    Map.of(
                            "challengeId", challengeId,
                            "evaluator", evaluator,
                            "entityType", "drill-challenge",
                            "entityId", challengeId,
                            "idempotencyKey", idempotencyKey
                    )
            ));
            deduplicated = false;
        }

        challenge.setEvaluationJobId(jobId);
        challengeRepository.save(challenge);

        return ResponseEntityBuilder.getBuilder(HttpStatus.ACCEPTED).successResponse(
                ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION,
                Map.of(
                        "message", "The evaluation job has been successfully submitted.",
                        "jobId", jobId,
                        "deduplicated", deduplicated,
                        "challengeId", challengeId,
                        "statusUrl", ASYNC_URL + "/" + jobId,
                        "searchUrl", ASYNC_URL + "/search?jobType=leximentor.drill.challenge.evaluate&metadataKey=entityId&metadataValue=" + challengeId
                )
        );
    }

    @GetMapping(value = {"/{challengeId}/evaluations/status", "/{challengeId}/evaluation-status"}, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> getEvaluationStatusByChallenge(@PathVariable String challengeId) {
        Challenge challenge = challengeRepository.findByKey(challengeId);
        if (challenge == null || challenge.getEvaluationJobId() == null || challenge.getEvaluationJobId().isBlank()) {
            return ResponseEntityBuilder.getBuilder(HttpStatus.NOT_FOUND)
                    .errorResponse(REQUEST_FAILURE_CODE, "No async job found for this challenge.");
        }

        Optional<JobSnapshot> snapshot = jobClient.getJob(challenge.getEvaluationJobId());
        if (snapshot.isEmpty()) {
            return ResponseEntityBuilder.getBuilder(HttpStatus.NOT_FOUND)
                    .errorResponse(REQUEST_FAILURE_CODE, "Async job id is not found.");
        }
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(
                ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION,
                Map.of(
                        "challengeId", challengeId,
                        "jobId", challenge.getEvaluationJobId(),
                        "statusUrl", ASYNC_URL + "/" + challenge.getEvaluationJobId(),
                        "job", snapshot.get()
                )
        );
    }

    @GetMapping(value = {"/{challengeId}/evaluations/report", "/{challengeId}/report"}, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> getEvaluationReport(@PathVariable String challengeId) {
        log.info("Received a request to get the evaluation report for the challenge {}", challengeId);
        ChallengeReportResponseDTO reportResponseDTO = challengeEvaluationService.getEvaluationReport(challengeId);
        log.info("Found the report for the challenge id {},{}", challengeId, reportResponseDTO);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, reportResponseDTO);
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
}
