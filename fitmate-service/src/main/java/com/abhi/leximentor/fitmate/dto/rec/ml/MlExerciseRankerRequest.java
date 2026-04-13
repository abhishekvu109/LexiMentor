package com.abhi.leximentor.fitmate.dto.rec.ml;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Mirrors Python schema {@code ExerciseRankerRequest} in fitmate-ml.
 * Sent to {@code POST /predict/exercises}.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MlExerciseRankerRequest {

    private String username;

    @JsonProperty("target_body_part_ref_id")
    private long targetBodyPartRefId;

    /** Null when no training-type filter was specified. */
    @JsonProperty("training_ref_id")
    private Long trainingRefId;

    private List<MlExerciseFeatures> candidates;
}
