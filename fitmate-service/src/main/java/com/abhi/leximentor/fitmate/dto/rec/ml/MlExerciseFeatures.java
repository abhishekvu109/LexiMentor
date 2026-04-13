package com.abhi.leximentor.fitmate.dto.rec.ml;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * Mirrors Python schema {@code ExerciseFeatures} in fitmate-ml.
 * Sent as part of {@link MlExerciseRankerRequest}.
 */
@Data
@Builder
public class MlExerciseFeatures {

    @JsonProperty("exercise_ref_id")
    private long exerciseRefId;

    @JsonProperty("exercise_name")
    private String exerciseName;

    /** 999 when the user has never performed this exercise. */
    @JsonProperty("days_since_last_done")
    private int daysSinceLastDone;

    @JsonProperty("frequency_last_30_days")
    private int frequencyLast30Days;

    /** Percentage weight change over available history (e.g. 0.12 = +12%). */
    @JsonProperty("progression_rate")
    private double progressionRate;

    @JsonProperty("difficulty_level")
    private int difficultyLevel;

    /** 1.0 = exact body-part match; 0.5 = related; 0.0 = unrelated. */
    @JsonProperty("body_part_match_score")
    private double bodyPartMatchScore;
}
