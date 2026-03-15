package com.abhi.leximentor.fitmate.dto.rec.ml;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Mirrors Python schema {@code PerformancePrediction} in fitmate-ml.
 * One element inside {@link MlPerformancePredictorResponse#getPredictions()}.
 */
@Data
@NoArgsConstructor
public class MlPerformancePrediction {

    @JsonProperty("exercise_ref_id")
    private long exerciseRefId;

    @JsonProperty("recommended_weight")
    private double recommendedWeight;

    @JsonProperty("recommended_sets")
    private int recommendedSets;

    @JsonProperty("recommended_reps")
    private int recommendedReps;

    /** Model confidence in [0, 1]. */
    private double confidence;

    @JsonProperty("progression_note")
    private String progressionNote;
}
