package com.abhi.leximentor.inventory.service.drill.impl.strategy;

import com.abhi.leximentor.inventory.constants.DrillTypes;
import com.abhi.leximentor.inventory.dto.drill.DrillChallengeScoresDTO;
import com.abhi.leximentor.inventory.dto.drill.DrillEvaluationDTO;
import com.abhi.leximentor.inventory.entities.drill.DrillChallenge;
import com.abhi.leximentor.inventory.service.evaluation.SentenceDrillEvaluator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SentenceUsageEvaluationStrategy implements DrillEvaluationStrategy {
    private final SentenceDrillEvaluator sentenceDrillEvaluator;

    @Override
    public DrillTypes getType() {
        return DrillTypes.SENTENCE_USAGE;
    }

    @Override
    public List<DrillEvaluationDTO> evaluate(List<DrillChallengeScoresDTO> drillChallengeScoresDTOS, DrillChallenge drillChallenge, String evaluator) {
        return sentenceDrillEvaluator.init(drillChallengeScoresDTOS, drillChallenge, evaluator)
                .evaluate()
                .getResult();
    }
}
