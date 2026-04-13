package com.abhi.leximentor.leximentor.service.drill.impl.strategy;

import com.abhi.leximentor.leximentor.constants.ChallengeType;
import com.abhi.leximentor.leximentor.dto.drill.ChallengeScoresDTO;
import com.abhi.leximentor.leximentor.dto.drill.ChallengeEvaluationDTO;
import com.abhi.leximentor.leximentor.entities.drill.Challenge;
import com.abhi.leximentor.leximentor.service.evaluation.MeaningDrillEvaluator;
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
    public ChallengeType getType() {
        return ChallengeType.LEARN_MEANING;
    }

    @Override
    public List<ChallengeEvaluationDTO> evaluate(List<ChallengeScoresDTO> ChallengeScoresDTOS, Challenge challenge, String evaluator) {
        return meaningDrillEvaluator.init(ChallengeScoresDTOS, challenge, LLM_URL, MODEL_NAME, evaluator)
                .evaluate()
                .getResult();
    }
}
