package com.abhi.leximentor.leximentor.service.drill.impl.strategy;

import com.abhi.leximentor.leximentor.constants.ChallengeType;
import com.abhi.leximentor.leximentor.dto.drill.ChallengeScoresDTO;
import com.abhi.leximentor.leximentor.dto.drill.ChallengeEvaluationDTO;
import com.abhi.leximentor.leximentor.entities.drill.Challenge;

import java.util.List;

public interface DrillEvaluationStrategy {
    ChallengeType getType();

    List<ChallengeEvaluationDTO> evaluate(List<ChallengeScoresDTO> ChallengeScoresDTOS, Challenge challenge, String evaluator);
}
