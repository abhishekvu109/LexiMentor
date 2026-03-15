package com.abhi.leximentor.fitmate.dto.rec.ml;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Mirrors Python schema {@code RankedExercise} in fitmate-ml.
 * One element inside {@link MlExerciseRankerResponse#getRankedExercises()}.
 */
@Data
@NoArgsConstructor
public class MlRankedExercise {

    @JsonProperty("exercise_ref_id")
    private long exerciseRefId;

    @JsonProperty("exercise_name")
    private String exerciseName;

    /** 1-based rank (1 = highest priority). */
    private int rank;

    /** Raw relevance score from the XGBoost ranker. */
    private double score;
}
