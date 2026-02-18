package com.abhi.leximentor.leximentor.dto.drill;

import lombok.*;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeEvaluationDTO {
    private long refId;
    private ChallengeScoresDTO ChallengeScoresDTO;
    private String evaluator;
    private double confidence;
    private String reason;
    private double evaluationTime;
}
