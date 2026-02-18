package com.abhi.leximentor.leximentor.config;

import com.abhi.asyncjobs.api.JobDefinition;
import com.abhi.asyncjobs.retry.ExponentialBackoffRetryPolicy;
import com.abhi.leximentor.leximentor.dto.drill.ChallengeEvaluationJobPayload;
import com.abhi.leximentor.leximentor.dto.drill.ChallengeScoresDTO;
import com.abhi.leximentor.leximentor.entities.drill.Challenge;
import com.abhi.leximentor.leximentor.repository.drill.DrillChallengeRepository;
import com.abhi.leximentor.leximentor.service.drill.DrillChallengeScoreService;
import com.abhi.leximentor.leximentor.service.drill.DrillEvaluationService;
import com.abhi.asyncjobs.starter.registration.AsyncJobRegistration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Configuration
public class AsyncJobsRegistrationConfig {

    @Bean
    public AsyncJobRegistration inventoryAsyncJobRegistration(
        DrillChallengeRepository drillChallengeRepository,
        DrillChallengeScoreService drillChallengeScoreService,
        DrillEvaluationService drillEvaluationService
    ) {
        return jobClient -> {
            jobClient.register(
                JobDefinition.builder("leximentor.word-metadata.refresh", Map.class, ctx -> Map.of(
                        "jobId", ctx.jobId(),
                        "app", "leximentor",
                        "status", "processed",
                        "attempt", ctx.attempt(),
                        "payload", ctx.payload()
                    ))
                    .retryPolicy(new ExponentialBackoffRetryPolicy(
                        4,
                        Duration.ofMillis(500),
                        2.0,
                        Duration.ofSeconds(8)
                    ))
                    .build()
            );

            jobClient.register(
                JobDefinition.builder("leximentor.drill.challenge.evaluate", ChallengeEvaluationJobPayload.class, ctx -> {
                        ChallengeEvaluationJobPayload payload = ctx.payload();
                        Challenge challenge = drillChallengeRepository.findByRefId(payload.getChallengeRefId());
                        List<ChallengeScoresDTO> scores = drillChallengeScoreService.getByDrillChallengeId(challenge);
                        drillEvaluationService.evaluate(scores, payload.getEvaluator(), payload.getChallengeRefId());
                        return Map.of(
                            "jobId", ctx.jobId(),
                            "challengeRefId", payload.getChallengeRefId(),
                            "evaluator", payload.getEvaluator(),
                            "evaluatedQuestions", scores.size()
                        );
                    })
                    .retryPolicy(new ExponentialBackoffRetryPolicy(
                        3,
                        Duration.ofSeconds(1),
                        2.0,
                        Duration.ofSeconds(10)
                    ))
                    .build()
            );
        };
    }
}
