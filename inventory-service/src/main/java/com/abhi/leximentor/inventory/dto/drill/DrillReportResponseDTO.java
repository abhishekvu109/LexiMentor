package com.abhi.leximentor.inventory.dto.drill;

import lombok.*;

import java.util.List;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DrillReportResponseDTO {
    private String challengeRefId;
    private String evaluator;
    private String drillType;
    private List<DrillEvaluationDTO> drillEvaluationDTOS;
    private double score;
    private int totalCorrect;
    private int totalIncorrect;
    private boolean isPassed;
}
