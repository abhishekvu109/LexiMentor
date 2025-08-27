package com.abhi.writewise.inventory.entities.nosql.mongodb.evaluation;

import lombok.*;

import java.util.List;

@Data
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class EvaluationResult {
    private long refId;
    private String uuid;
    private EvaluationMetric spelling;
    private EvaluationMetric grammar;
    private EvaluationMetric punctuation;
    private EvaluationMetric vocabulary;
    private EvaluationMetric styleAndTone;
    private EvaluationMetric creativityAndThinking;
    private double score;
    private List<String> OverallRecommendations;
}
