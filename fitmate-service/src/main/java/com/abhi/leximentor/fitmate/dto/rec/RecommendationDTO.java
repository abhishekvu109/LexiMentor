package com.abhi.leximentor.fitmate.dto.rec;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Top-level recommendation response returned to the client.
 */
@Data
@Builder
public class RecommendationDTO {

    private String username;

    /** Name of the body part targeted in this recommendation. */
    private String targetBodyPart;

    /** Name of the training type used for filtering (null if not filtered). */
    private String trainingType;

    /** Ordered list of recommended exercises (highest priority first). */
    private List<ExerciseRecommendationDTO> recommendations;

    /** Timestamp when this recommendation was generated. */
    private LocalDateTime generatedAt;

    /**
     * Engine version tag for observability.
     * {@code "RULE_ENGINE_V1"} → Phase 1 | {@code "ML_MODEL_V1"} → Phase 2
     */
    private String engineVersion;
}
