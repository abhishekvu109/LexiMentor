package com.abhi.leximentor.leximentor.service.drill.impl.strategy;

import com.abhi.leximentor.leximentor.constants.DrillTypes;
import com.abhi.leximentor.leximentor.dto.drill.ChallengeScoresDTO;
import com.abhi.leximentor.leximentor.entities.drill.ChallengeScores;
import com.abhi.leximentor.leximentor.entities.drill.DrillSet;
import com.abhi.leximentor.leximentor.entities.inv.WordMetadata;
import com.abhi.leximentor.leximentor.repository.drill.DrillChallengeRepository;
import com.abhi.leximentor.leximentor.repository.drill.DrillChallengeScoreRepository;
import com.abhi.leximentor.leximentor.repository.drill.DrillEvaluationRepository;
import com.abhi.leximentor.leximentor.repository.drill.DrillSetRepository;
import com.abhi.leximentor.leximentor.repository.inv.EvaluatorRepository;
import org.springframework.stereotype.Service;

@Service
public class PosEvaluationStrategy extends AbstractSimpleEvaluationStrategy {
    public PosEvaluationStrategy(DrillSetRepository drillSetRepository,
                                 DrillChallengeScoreRepository drillChallengeScoreRepository,
                                 DrillChallengeRepository drillChallengeRepository,
                                 EvaluatorRepository evaluatorRepository,
                                 DrillEvaluationRepository drillEvaluationRepository) {
        super(drillSetRepository, drillChallengeScoreRepository, drillChallengeRepository, evaluatorRepository, drillEvaluationRepository);
    }

    @Override
    public DrillTypes getType() {
        return DrillTypes.LEARN_POS;
    }

    @Override
    protected boolean isCorrect(ChallengeScoresDTO dto, ChallengeScores scores, DrillSet drillSet, WordMetadata wordMetadata) {
        return wordMetadata.getPartsOfSpeeches().stream()
                .anyMatch(partsOfSpeech -> partsOfSpeech.getPos().equalsIgnoreCase(scores.getResponse()));
    }
}
