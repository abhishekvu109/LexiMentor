package com.abhi.leximentor.fitmate.dto.rec.ml;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Mirrors Python schema {@code PerformancePredictorResponse} in fitmate-ml.
 * Received from {@code POST /predict/performance}.
 */
@Data
@NoArgsConstructor
public class MlPerformancePredictorResponse {

    private List<MlPerformancePrediction> predictions;

    @JsonProperty("model_version")
    private String modelVersion;
}
