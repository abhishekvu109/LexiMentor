package com.abhi.leximentor.inventory.dto.drill;

import lombok.*;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DrillEvaluationDTO {
    private long refId;
    private DrillChallengeScoresDTO drillChallengeScoresDTO;
    private String evaluator;
    private double confidence;
    private String reason;
    private double evaluationTime;
}
