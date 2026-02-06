package com.abhi.leximentor.inventory.service.drill.impl.strategy;

import com.abhi.leximentor.inventory.constants.DrillTypes;
import com.abhi.leximentor.inventory.dto.drill.DrillChallengeScoresDTO;
import com.abhi.leximentor.inventory.dto.drill.DrillEvaluationDTO;
import com.abhi.leximentor.inventory.entities.drill.DrillChallenge;
import com.abhi.leximentor.inventory.service.evaluation.MeaningDrillEvaluator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MeaningEvaluationStrategy implements DrillEvaluationStrategy {
    private final MeaningDrillEvaluator meaningDrillEvaluator;

    @Value("${ollama-llm-writing-module-topics}")
    private String LLM_URL;

    @Value("${model-name}")
    private String MODEL_NAME;

    @Override
    public DrillTypes getType() {
        return DrillTypes.LEARN_MEANING;
    }

    @Override
    public List<DrillEvaluationDTO> evaluate(List<DrillChallengeScoresDTO> drillChallengeScoresDTOS, DrillChallenge drillChallenge, String evaluator) {
        return meaningDrillEvaluator.init(drillChallengeScoresDTOS, drillChallenge, LLM_URL, MODEL_NAME, evaluator)
                .evaluate()
                .getResult();
    }
}
