package com.abhi.leximentor.fitmate.dto.rec;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Input for the workout recommendation engine.
 *
 * <p>At minimum, {@code username} and {@code targetBodyPartRefId} are required.
 * All other fields are optional and narrow the recommendation further.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationRequestDTO {

    /** The user to generate recommendations for. */
    private String username;

    /** RefId of the body part to train (e.g. chest, legs). Required. */
    private String targetBodyPartRefId;

    /**
     * Optional: RefId of the training type (e.g. STRENGTH, HYPERTROPHY).
     * When supplied, only exercises belonging to that training type are considered.
     */
    private String trainingRefId;

    /**
     * Maximum number of exercises to return.
     * Defaults to 5 if not set or set to 0.
     */
    @Builder.Default
    private int maxExercises = 5;
}
