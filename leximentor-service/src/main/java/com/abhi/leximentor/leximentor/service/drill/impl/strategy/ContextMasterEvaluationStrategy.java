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
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class ContextMasterEvaluationStrategy extends AbstractSimpleEvaluationStrategy {
    public ContextMasterEvaluationStrategy(DrillSetRepository drillSetRepository,
                                           DrillChallengeScoreRepository drillChallengeScoreRepository,
                                           DrillChallengeRepository drillChallengeRepository,
                                           EvaluatorRepository evaluatorRepository,
                                           DrillEvaluationRepository drillEvaluationRepository) {
        super(drillSetRepository, drillChallengeScoreRepository, drillChallengeRepository, evaluatorRepository, drillEvaluationRepository);
    }

    @Override
    public DrillTypes getType() {
        return DrillTypes.CONTEXT_MASTER;
    }

    @Override
    protected boolean requiresWordMetadata() {
        return false;
    }

    @Override
    protected boolean isCorrect(ChallengeScoresDTO dto, ChallengeScores scores, DrillSet drillSet, WordMetadata wordMetadata) {
        return StringUtils.equalsIgnoreCase(scores.getQuestion(), scores.getResponse());
    }
}
