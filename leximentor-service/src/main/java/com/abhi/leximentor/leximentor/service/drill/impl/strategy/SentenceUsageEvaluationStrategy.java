package com.abhi.leximentor.leximentor.service.drill.impl.strategy;

import com.abhi.leximentor.leximentor.constants.ChallengeType;
import com.abhi.leximentor.leximentor.dto.drill.ChallengeScoresDTO;
import com.abhi.leximentor.leximentor.dto.drill.ChallengeEvaluationDTO;
import com.abhi.leximentor.leximentor.entities.drill.Challenge;
import com.abhi.leximentor.leximentor.service.evaluation.SentenceDrillEvaluator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SentenceUsageEvaluationStrategy implements DrillEvaluationStrategy {
    private final SentenceDrillEvaluator sentenceDrillEvaluator;

    @Override
    public ChallengeType getType() {
        return ChallengeType.SENTENCE_USAGE;
    }

    @Override
    public List<ChallengeEvaluationDTO> evaluate(List<ChallengeScoresDTO> ChallengeScoresDTOS, Challenge challenge, String evaluator) {
        return sentenceDrillEvaluator.init(ChallengeScoresDTOS, challenge, evaluator)
                .evaluate()
                .getResult();
    }
}
