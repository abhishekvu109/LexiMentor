package com.abhi.leximentor.inventory.service.drill.impl.strategy;

import com.abhi.leximentor.inventory.constants.DrillTypes;
import com.abhi.leximentor.inventory.dto.drill.DrillChallengeScoresDTO;
import com.abhi.leximentor.inventory.dto.drill.DrillEvaluationDTO;
import com.abhi.leximentor.inventory.entities.drill.DrillChallenge;

import java.util.List;

public interface DrillEvaluationStrategy {
    DrillTypes getType();

    List<DrillEvaluationDTO> evaluate(List<DrillChallengeScoresDTO> drillChallengeScoresDTOS, DrillChallenge drillChallenge, String evaluator);
}
