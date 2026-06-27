package com.abhi.leximentor.fitmate.service.impl;

import com.abhi.leximentor.fitmate.dto.NutritionAiMealInputDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.annotation.PostConstruct;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

/**
 * Calls the local Ollama LLM to estimate nutritional values for a given meal input.
 *
 * <p>Wrapped with a <strong>Resilience4j circuit breaker</strong> named
 * {@value #CIRCUIT_BREAKER_NAME}. If Ollama becomes unreachable or consistently
 * slow / erroneous, the circuit opens and calls are short-circuited immediately
 * (without hitting the network) until the service recovers.
 *
 * <p>Circuit breaker configuration is driven from {@code application.yml} under
 * {@code resilience4j.circuitbreaker.instances.nutritionAiEstimator}.
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NutritionAiEstimator {

    static final String CIRCUIT_BREAKER_NAME = "nutritionAiEstimator";

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;
    /**
     * Injected by Resilience4j auto-configuration; used only to attach event listeners
     * for structured logging of circuit state transitions.
     */
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @Value("${fitmate.ai.ollama.url:http://192.168.1.90:11434/api/generate}")
    private String ollamaUrl;

    @Value("${fitmate.ai.ollama.model:ministral-3:8b}")
    private String ollamaModel;

    // ──────────────────────────────────────────────────────────────────────────
    // Circuit breaker event listeners (for structured logging / observability)
    // ──────────────────────────────────────────────────────────────────────────

    @PostConstruct
    void initCircuitBreakerEvents() {
        circuitBreakerRegistry.circuitBreaker(CIRCUIT_BREAKER_NAME)
                .getEventPublisher()
                .onStateTransition(e ->
                        log.warn("[NutritionAI CB] State transition: {}", e.getStateTransition()))
                .onCallNotPermitted(e ->
                        log.warn("[NutritionAI CB] Call blocked — circuit is OPEN; Ollama will not be contacted"))
                .onError(e ->
                        log.error("[NutritionAI CB] Failure recorded ({}): {}",
                                e.getElapsedDuration(), e.getThrowable().getMessage()))
                .onSlowCallRateExceeded(e ->
                        log.warn("[NutritionAI CB] Slow-call rate exceeded: {:.1f}%", e.getSlowCallRate()))
                .onFailureRateExceeded(e ->
                        log.warn("[NutritionAI CB] Failure rate exceeded: {:.1f}%", e.getFailureRate()))
                .onSuccess(e ->
                        log.debug("[NutritionAI CB] Successful call in {}ms", e.getElapsedDuration().toMillis()));
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Public API
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Estimates nutrition for the given meal by calling Ollama.
     *
     * <p>The method is guarded by the {@value #CIRCUIT_BREAKER_NAME} circuit breaker:
     * <ul>
     *   <li>While the circuit is <strong>CLOSED</strong> — the HTTP call is made normally.</li>
     *   <li>While the circuit is <strong>OPEN</strong> — {@link #estimateFallback} is invoked
     *       immediately without hitting the network.</li>
     *   <li>In <strong>HALF-OPEN</strong> — a limited number of probe calls are allowed through
     *       to test recovery.</li>
     * </ul>
     * Any exception thrown here is recorded as a failure by the circuit breaker before
     * the fallback is invoked.
     *
     * @param input the meal details (food name, quantity, unit, notes)
     * @return the estimated {@link NutritionEstimate}
     * @throws IllegalStateException if the AI call fails or the circuit is open
     */
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "estimateFallback")
    public NutritionEstimate estimate(NutritionAiMealInputDTO input) {
        log.debug("[NutritionAI] Calling Ollama for food='{}' qty={} unit='{}'",
                input.getFoodName(), input.getServingQty(), input.getServingUnit());
        try {
            Map<String, Object> requestBody = Map.of(
                    "model", ollamaModel,
                    "stream", false,
                    "format", buildFormat(),
                    "prompt", buildPrompt(input)
            );

            String rawResponse = webClientBuilder.build()
                    .post()
                    .uri(ollamaUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(),
                            resp -> resp.bodyToMono(String.class)
                                    .map(body -> new IllegalStateException(
                                            "Ollama returned non-2xx: " + resp.statusCode() + " — " + body)))
                    .bodyToMono(String.class)
                    .blockOptional()
                    .orElseThrow(() -> new IllegalStateException("Ollama returned an empty body"));

            JsonNode root = objectMapper.readTree(rawResponse);
            JsonNode responseNode = root.get("response");
            if (responseNode == null || responseNode.isNull()) {
                throw new IllegalStateException("Ollama payload missing 'response' field");
            }

            JsonNode parsed = objectMapper.readTree(responseNode.asText());
            JsonNode nutrition = parsed.path("estimatedNutrition");

            return NutritionEstimate.builder()
                    .calories(nutrition.path("calories").asDouble(0d))
                    .protein(nutrition.path("protein").asDouble(0d))
                    .carbs(nutrition.path("carbs").asDouble(0d))
                    .fat(nutrition.path("fat").asDouble(0d))
                    .fiber(nutrition.path("fiber").asDouble(0d))
                    .sugar(nutrition.path("sugar").asDouble(0d))
                    .sodium(nutrition.path("sodium").asDouble(0d))
                    .assumptions(parsed.has("assumptions")
                            ? objectMapper.convertValue(parsed.get("assumptions"), List.class)
                            : List.of())
                    .confidence(parsed.path("confidence").isNumber()
                            ? parsed.path("confidence").asInt(0)
                            : 0)
                    .build();

        } catch (Exception ex) {
            log.error("[NutritionAI] Ollama call failed for food='{}': {}", input.getFoodName(), ex.getMessage());
            // Re-throw so Resilience4j records this as a circuit-breaker failure
            throw new IllegalStateException("Failed to estimate nutrition from AI: " + ex.getMessage(), ex);
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Circuit breaker fallback
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Fallback invoked by Resilience4j when:
     * <ol>
     *   <li>{@link #estimate} throws any {@link Exception} (failure recorded, CB stays closed
     *       until the threshold is exceeded), OR</li>
     *   <li>The circuit breaker is <strong>OPEN</strong> (short-circuited without calling
     *       {@link #estimate} at all).</li>
     * </ol>
     *
     * <p>Signature must match {@link #estimate} plus a trailing {@link Exception} parameter.
     * We rethrow as {@link IllegalStateException} so {@link NutritionAiBatchWorker} catches it
     * and marks the meal entry as {@code FAILED} — the existing error-handling path is reused.
     */
    private NutritionEstimate estimateFallback(NutritionAiMealInputDTO input, Exception ex) {
        String reason = ex.getClass().getSimpleName() + ": " + ex.getMessage();
        log.warn("[NutritionAI CB] Fallback triggered for food='{}'. Reason: {}", input.getFoodName(), reason);
        throw new IllegalStateException(
                "AI nutrition service is temporarily unavailable — please try again later. (" + reason + ")", ex);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Prompt / format builders
    // ──────────────────────────────────────────────────────────────────────────

    private String buildPrompt(NutritionAiMealInputDTO input) {
        return String.join("\n",
                "You are a certified sports nutrition assistant.",
                "Return ONLY valid JSON matching the provided format schema.",
                "No markdown, no code fences, no extra text.",
                "Estimate nutrition values from the user input. Use realistic values per serving and quantity.",
                "If details are ambiguous, make conservative assumptions and list them clearly.",
                "All nutrition values must be non-negative numbers.",
                "",
                "Input:",
                "<Request>",
                "{\"foodName\":\"" + safe(input.getFoodName())
                        + "\",\"servingQty\":" + input.getServingQty()
                        + ",\"servingUnit\":\"" + safe(input.getServingUnit())
                        + "\",\"notes\":\"" + safe(input.getNotes()) + "\"}",
                "</Request>"
        );
    }

    private Map<String, Object> buildFormat() {
        return Map.of(
                "type", "object",
                "properties", Map.of(
                        "foodName", Map.of("type", "string"),
                        "servingDescription", Map.of("type", "string"),
                        "estimatedNutrition", Map.of(
                                "type", "object",
                                "properties", Map.of(
                                        "calories", Map.of("type", "number", "minimum", 0),
                                        "protein", Map.of("type", "number", "minimum", 0),
                                        "carbs", Map.of("type", "number", "minimum", 0),
                                        "fat", Map.of("type", "number", "minimum", 0),
                                        "fiber", Map.of("type", "number", "minimum", 0),
                                        "sugar", Map.of("type", "number", "minimum", 0),
                                        "sodium", Map.of("type", "number", "minimum", 0)
                                ),
                                "required", List.of("calories", "protein", "carbs", "fat", "fiber", "sugar", "sodium")
                        ),
                        "assumptions", Map.of("type", "array", "items", Map.of("type", "string")),
                        "confidence", Map.of("type", "integer", "minimum", 0, "maximum", 100)
                ),
                "required", List.of("foodName", "servingDescription", "estimatedNutrition", "assumptions", "confidence")
        );
    }

    private String safe(String value) {
        return value == null ? "" : value.replace("\"", "\\\"");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Result model
    // ──────────────────────────────────────────────────────────────────────────

    @Data
    @Builder
    public static class NutritionEstimate {
        private double calories;
        private double protein;
        private double carbs;
        private double fat;
        private double fiber;
        private double sugar;
        private double sodium;
        private List<String> assumptions;
        private Integer confidence;
    }
}
