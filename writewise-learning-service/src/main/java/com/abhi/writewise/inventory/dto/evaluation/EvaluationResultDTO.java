package com.abhi.writewise.inventory.dto.evaluation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Data
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EvaluationResultDTO {
    private String refId;
    private String uuid;
    private EvaluationMetricDTO spelling;
    private EvaluationMetricDTO grammar;
    private EvaluationMetricDTO punctuation;
    private EvaluationMetricDTO vocabulary;
    private EvaluationMetricDTO styleAndTone;
    private EvaluationMetricDTO creativityAndThinking;
    private double score;
    private List<String> comments;
}
