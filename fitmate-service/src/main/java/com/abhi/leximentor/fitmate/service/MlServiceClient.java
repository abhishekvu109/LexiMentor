package com.abhi.leximentor.fitmate.service;

import com.abhi.leximentor.fitmate.dto.rec.ml.MlExerciseRankerRequest;
import com.abhi.leximentor.fitmate.dto.rec.ml.MlExerciseRankerResponse;
import com.abhi.leximentor.fitmate.dto.rec.ml.MlPerformanceFeatures;
import com.abhi.leximentor.fitmate.dto.rec.ml.MlPerformancePredictorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

/**
 * Thin facade over the fitmate-ml (Python FastAPI) REST API.
 *
 * <h3>Fallback contract</h3>
 * Every method returns {@code Optional.empty()} on <em>any</em> failure:
 * <ul>
 *   <li>503 — models not yet trained</li>
 *   <li>Connection refused / timeout — Python service is not running</li>
 *   <li>Any other 4xx / 5xx from the ML service</li>
 * </ul>
 * Callers ({@link com.abhi.leximentor.fitmate.service.impl.RecommendationServiceImpl})
 * treat {@code Optional.empty()} as a signal to fall back to the rule engine.
 * The failure is logged as WARN (expected while models are not yet trained).
 *
 * <h3>Blocking usage</h3>
 * The service is Spring MVC (not reactive), so we call {@code .block()} here.
 * This is safe because we run on a servlet thread — never on a Reactor scheduler.
 */
@Slf4j
@Component
public class MlServiceClient {

    private static final String PREDICT_EXERCISES_URI = "/predict/exercises";
    private static final String PREDICT_PERFORMANCE_URI = "/predict/performance";

    private final WebClient webClient;

    public MlServiceClient(@Qualifier("mlServiceWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    // ── Exercise ranker ───────────────────────────────────────────────────────

    /**
     * Ask the ML service to rank candidate exercises for a user.
     *
     * @param request pre-built request with feature vectors for each candidate
     * @return ranked list, or {@code Optional.empty()} if the ML service is unavailable
     */
    public Optional<MlExerciseRankerResponse> rankExercises(MlExerciseRankerRequest request) {
        try {
            MlExerciseRankerResponse response = webClient.post()
                    .uri(PREDICT_EXERCISES_URI)
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .flatMap(body -> {
                                        log.warn("[MlServiceClient] /predict/exercises returned {} — body: {}",
                                                clientResponse.statusCode(), body);
                                        return Mono.empty();
                                    })
                    )
                    .bodyToMono(MlExerciseRankerResponse.class)
                    .onErrorResume(WebClientRequestException.class, ex -> {
                        log.warn("[MlServiceClient] Cannot reach fitmate-ml for exercise ranking: {}", ex.getMessage());
                        return Mono.empty();
                    })
                    .onErrorResume(Exception.class, ex -> {
                        log.warn("[MlServiceClient] Exercise ranker error: {}", ex.getMessage());
                        return Mono.empty();
                    })
                    .block();

            return Optional.ofNullable(response);

        } catch (Exception ex) {
            log.warn("[MlServiceClient] Unexpected error calling exercise ranker: {}", ex.getMessage());
            return Optional.empty();
        }
    }

    // ── Performance predictor ─────────────────────────────────────────────────

    /**
     * Ask the ML service to predict recommended weight + reps for a batch of exercises.
     *
     * @param features one feature object per exercise
     * @return predictions keyed by exerciseRefId, or {@code Optional.empty()} if unavailable
     */
    public Optional<MlPerformancePredictorResponse> predictPerformance(List<MlPerformanceFeatures> features) {
        try {
            MlPerformancePredictorResponse response = webClient.post()
                    .uri(PREDICT_PERFORMANCE_URI)
                    .bodyValue(features)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .flatMap(body -> {
                                        log.warn("[MlServiceClient] /predict/performance returned {} — body: {}",
                                                clientResponse.statusCode(), body);
                                        return Mono.empty();
                                    })
                    )
                    .bodyToMono(MlPerformancePredictorResponse.class)
                    .onErrorResume(WebClientRequestException.class, ex -> {
                        log.warn("[MlServiceClient] Cannot reach fitmate-ml for performance prediction: {}", ex.getMessage());
                        return Mono.empty();
                    })
                    .onErrorResume(Exception.class, ex -> {
                        log.warn("[MlServiceClient] Performance predictor error: {}", ex.getMessage());
                        return Mono.empty();
                    })
                    .block();

            return Optional.ofNullable(response);

        } catch (Exception ex) {
            log.warn("[MlServiceClient] Unexpected error calling performance predictor: {}", ex.getMessage());
            return Optional.empty();
        }
    }
}
