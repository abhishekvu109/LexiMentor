package com.abhi.leximentor.leximentor.service.drill.impl.strategy;

import com.abhi.leximentor.leximentor.constants.ChallengeType;
import com.abhi.leximentor.leximentor.dto.drill.ChallengeScoresDTO;
import com.abhi.leximentor.leximentor.entities.drill.ChallengeScores;
import com.abhi.leximentor.leximentor.entities.drill.DrillSet;
import com.abhi.leximentor.leximentor.entities.inv.WordMetadata;
import com.abhi.leximentor.leximentor.repository.drill.ChallengeRepository;
import com.abhi.leximentor.leximentor.repository.drill.ChallengeScoreRepository;
import com.abhi.leximentor.leximentor.repository.drill.ChallengeEvaluationRepository;
import com.abhi.leximentor.leximentor.repository.drill.DrillSetRepository;
import com.abhi.leximentor.leximentor.repository.inv.EvaluatorRepository;
import org.springframework.stereotype.Service;

@Service
public class PosEvaluationStrategy extends AbstractSimpleEvaluationStrategy {
    public PosEvaluationStrategy(DrillSetRepository drillSetRepository,
                                 ChallengeScoreRepository challengeScoreRepository,
                                 ChallengeRepository challengeRepository,
                                 EvaluatorRepository evaluatorRepository,
                                 ChallengeEvaluationRepository challengeEvaluationRepository) {
        super(drillSetRepository, challengeScoreRepository, challengeRepository, evaluatorRepository, challengeEvaluationRepository);
    }

    @Override
    public ChallengeType getType() {
        return ChallengeType.LEARN_POS;
    }

    @Override
    protected boolean isCorrect(ChallengeScoresDTO dto, ChallengeScores scores, DrillSet drillSet, WordMetadata wordMetadata) {
        return wordMetadata.getPartsOfSpeeches().stream()
                .anyMatch(partsOfSpeech -> partsOfSpeech.getPos().equalsIgnoreCase(scores.getResponse()));
    }
}
