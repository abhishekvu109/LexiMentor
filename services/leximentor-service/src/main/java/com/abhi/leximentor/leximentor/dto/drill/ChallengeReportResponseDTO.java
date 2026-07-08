package com.abhi.leximentor.leximentor.dto.drill;

import lombok.*;

import java.util.List;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeReportResponseDTO {
    private String challengeKey;
    private String evaluator;
    private String challengeType;
    private List<ChallengeEvaluationDTO> challengeEvaluationDTOS;
    private double score;
    private int totalCorrect;
    private int totalIncorrect;
    private boolean isPassed;
}
