package com.abhi.leximentor.inventory.service.drill.impl.strategy;

import com.abhi.leximentor.inventory.constants.Status;
import com.abhi.leximentor.inventory.dto.drill.DrillChallengeScoresDTO;
import com.abhi.leximentor.inventory.dto.drill.DrillEvaluationDTO;
import com.abhi.leximentor.inventory.entities.drill.DrillChallenge;
import com.abhi.leximentor.inventory.entities.drill.DrillChallengeScores;
import com.abhi.leximentor.inventory.entities.drill.DrillEvaluation;
import com.abhi.leximentor.inventory.entities.drill.DrillSet;
import com.abhi.leximentor.inventory.entities.inv.Evaluator;
import com.abhi.leximentor.inventory.entities.inv.WordMetadata;
import com.abhi.leximentor.inventory.exceptions.entities.ServerException;
import com.abhi.leximentor.inventory.repository.drill.DrillChallengeRepository;
import com.abhi.leximentor.inventory.repository.drill.DrillChallengeScoreRepository;
import com.abhi.leximentor.inventory.repository.drill.DrillEvaluationRepository;
import com.abhi.leximentor.inventory.repository.drill.DrillSetRepository;
import com.abhi.leximentor.inventory.repository.inv.EvaluatorRepository;
import com.abhi.leximentor.inventory.service.drill.impl.DrillServiceUtil;
import com.abhi.leximentor.inventory.util.CollectionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractSimpleEvaluationStrategy implements DrillEvaluationStrategy {
    private final DrillSetRepository drillSetRepository;
    private final DrillChallengeScoreRepository drillChallengeScoreRepository;
    private final DrillChallengeRepository drillChallengeRepository;
    private final EvaluatorRepository evaluatorRepository;
    private final DrillEvaluationRepository drillEvaluationRepository;

    protected abstract boolean isCorrect(DrillChallengeScoresDTO dto, DrillChallengeScores scores, DrillSet drillSet, WordMetadata wordMetadata);

    protected boolean requiresWordMetadata() {
        return true;
    }

    @Override
    public List<DrillEvaluationDTO> evaluate(List<DrillChallengeScoresDTO> drillChallengeScoresDTOS, DrillChallenge drillChallenge, String evaluatorName) {
        log.info("Initiated the {} evaluation.", getType().name());
        List<DrillEvaluationDTO> drillEvaluationDTOS = new LinkedList<>();
        List<DrillChallengeScores> drillChallengeScores = new LinkedList<>();
        Map<String, DrillChallengeScores> scoreRefMap = new HashMap<>();
        int totalCorrect = 0;
        int totalIncorrect = 0;

        Evaluator evaluator = evaluatorRepository.findByDrillType(getType().name()).stream().findFirst()
                .orElseThrow(() -> new ServerException().new InternalError("No evaluator found for drill type: " + getType().name()));

        for (DrillChallengeScoresDTO dto : drillChallengeScoresDTOS) {
            DrillChallengeScores scores = drillChallengeScoreRepository.findByRefId(Long.parseLong(dto.getRefId()));
            DrillSet drillSet = null;
            WordMetadata wordMetadata = null;
            if (requiresWordMetadata()) {
                drillSet = drillSetRepository.findByRefId(Long.parseLong(dto.getDrillSetRefId()));
                wordMetadata = drillSet.getWordId();
            }

            boolean correct = isCorrect(dto, scores, drillSet, wordMetadata);
            scores.setCorrect(correct);
            totalCorrect += correct ? 1 : 0;
            totalIncorrect += correct ? 0 : 1;
            drillChallengeScores.add(scores);
            scoreRefMap.put(dto.getRefId(), scores);
            drillEvaluationDTOS.add(DrillEvaluationDTO.builder()
                    .drillChallengeScoresDTO(dto)
                    .reason(correct ? "The match is found." : "The match is not found.")
                    .confidence(100)
                    .evaluator(evaluator.getName())
                    .build());
        }

        drillChallengeScoreRepository.saveAll(drillChallengeScores);
        drillChallenge.setTotalCorrect(totalCorrect);
        drillChallenge.setTotalWrong(totalIncorrect);
        drillChallenge.setStatus(Status.DrillChallenge.EVALUATED);
        drillChallenge.setEvaluationStatus(Status.DrillChallenge.COMPLETED);
        drillChallenge.setDrillScore(DrillServiceUtil.DrillChallengeUtil.score(totalCorrect, totalIncorrect));
        drillChallenge.setPass(DrillServiceUtil.DrillChallengeUtil.isPass(drillChallenge.getDrillScore()));
        drillChallengeRepository.save(drillChallenge);

        return persistEvaluations(drillEvaluationDTOS, evaluator, scoreRefMap);
    }

    private List<DrillEvaluationDTO> persistEvaluations(List<DrillEvaluationDTO> dtos, Evaluator evaluator, Map<String, DrillChallengeScores> scoreRefMap) {
        List<DrillEvaluationDTO> saved = new LinkedList<>();
        for (DrillEvaluationDTO dto : dtos) {
            DrillChallengeScores scores = scoreRefMap.get(dto.getDrillChallengeScoresDTO().getRefId());
            DrillEvaluation drillEvaluation = this.getDrillEvaluation(dto, scores, evaluator);
            DrillEvaluation stored = drillEvaluationRepository.save(drillEvaluation);
            saved.add(DrillServiceUtil.DrillEvaluationUtil.buildDTO(stored, DrillServiceUtil.DrillChallengeScoreUtil.buildDTO(stored.getDrillChallengeScores())));
        }
        return saved;
    }

    private DrillEvaluation getDrillEvaluation(DrillEvaluationDTO dto, DrillChallengeScores drillChallengeScores, Evaluator evaluator) {
        List<DrillEvaluation> drillEvaluations = drillEvaluationRepository.findByDrillChallengeScoresIn(List.of(drillChallengeScores));
        DrillEvaluation drillEvaluation;
        if (CollectionUtil.isNotEmpty(drillEvaluations)) {
            drillEvaluation = drillEvaluations.get(0);
            drillEvaluation.setEvaluator(evaluator);
            drillEvaluation.setEvaluationTime(dto.getEvaluationTime());
            drillEvaluation.setConfidence(dto.getConfidence());
            drillEvaluation.setDrillChallengeScores(drillChallengeScores);
            drillEvaluation.setReason(dto.getReason());
            return drillEvaluation;
        } else {
            return DrillServiceUtil.DrillEvaluationUtil.buildEntity(dto, evaluator, drillChallengeScores);
        }
    }
}
