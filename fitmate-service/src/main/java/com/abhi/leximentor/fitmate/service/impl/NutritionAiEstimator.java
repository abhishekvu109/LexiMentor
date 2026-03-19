package com.abhi.leximentor.fitmate.service.impl;

import com.abhi.leximentor.fitmate.dto.NutritionAiMealInputDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NutritionAiEstimator {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${fitmate.ai.ollama.url:http://192.168.1.90:11434/api/generate}")
    private String ollamaUrl;

    @Value("${fitmate.ai.ollama.model:ministral-3:8b}")
    private String ollamaModel;

    public NutritionEstimate estimate(NutritionAiMealInputDTO input) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = Map.of(
                    "model", ollamaModel,
                    "stream", false,
                    "format", buildFormat(),
                    "prompt", buildPrompt(input)
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(ollamaUrl, entity, String.class);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new IllegalStateException("AI response was empty");
            }

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode responseNode = root.get("response");
            if (responseNode == null || responseNode.isNull()) {
                throw new IllegalStateException("AI response field is missing");
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
                    .assumptions(parsed.has("assumptions") ? objectMapper.convertValue(parsed.get("assumptions"), List.class) : List.of())
                    .confidence(parsed.path("confidence").isNumber() ? parsed.path("confidence").asInt(0) : 0)
                    .build();
        } catch (Exception ex) {
            log.error("Failed to estimate nutrition from AI", ex);
            throw new IllegalStateException("Failed to estimate nutrition from AI");
        }
    }

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
                "{\"foodName\":\"" + safe(input.getFoodName()) + "\",\"servingQty\":" + input.getServingQty() + ",\"servingUnit\":\"" + safe(input.getServingUnit()) + "\",\"notes\":\"" + safe(input.getNotes()) + "\"}",
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
