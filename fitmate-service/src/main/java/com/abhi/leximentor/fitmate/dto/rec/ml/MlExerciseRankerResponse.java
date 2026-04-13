package com.abhi.leximentor.fitmate.dto.rec.ml;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Mirrors Python schema {@code ExerciseRankerResponse} in fitmate-ml.
 * Received from {@code POST /predict/exercises}.
 */
@Data
@NoArgsConstructor
public class MlExerciseRankerResponse {

    private String username;

    @JsonProperty("ranked_exercises")
    private List<MlRankedExercise> rankedExercises;

    @JsonProperty("model_version")
    private String modelVersion;
}
