package com.abhi.leximentor.inventory.service.drill.impl.strategy;

import com.abhi.leximentor.inventory.constants.DrillTypes;
import com.abhi.leximentor.inventory.dto.drill.DrillChallengeScoresDTO;
import com.abhi.leximentor.inventory.entities.drill.DrillChallengeScores;
import com.abhi.leximentor.inventory.entities.drill.DrillSet;
import com.abhi.leximentor.inventory.entities.inv.WordMetadata;
import com.abhi.leximentor.inventory.repository.drill.DrillChallengeRepository;
import com.abhi.leximentor.inventory.repository.drill.DrillChallengeScoreRepository;
import com.abhi.leximentor.inventory.repository.drill.DrillEvaluationRepository;
import com.abhi.leximentor.inventory.repository.drill.DrillSetRepository;
import com.abhi.leximentor.inventory.repository.inv.EvaluatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WordScrambleEvaluationStrategy extends AbstractSimpleEvaluationStrategy {
    public WordScrambleEvaluationStrategy(DrillSetRepository drillSetRepository,
                                          DrillChallengeScoreRepository drillChallengeScoreRepository,
                                          DrillChallengeRepository drillChallengeRepository,
                                          EvaluatorRepository evaluatorRepository,
                                          DrillEvaluationRepository drillEvaluationRepository) {
        super(drillSetRepository, drillChallengeScoreRepository, drillChallengeRepository, evaluatorRepository, drillEvaluationRepository);
    }

    @Override
    public DrillTypes getType() {
        return DrillTypes.WORD_SCRAMBLE;
    }

    @Override
    protected boolean requiresWordMetadata() {
        return false;
    }

    @Override
    protected boolean isCorrect(DrillChallengeScoresDTO dto, DrillChallengeScores scores, DrillSet drillSet, WordMetadata wordMetadata) {
        return dto.isCorrect();
    }
}
