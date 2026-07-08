package com.abhi.leximentor.leximentor.dto.drill;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ChallengeEvaluationJobPayload {
    private String challengeKey;
    private String evaluator;
}
