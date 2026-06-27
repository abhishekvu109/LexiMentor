package com.abhi.leximentor.inventory.service.drill.impl;

import com.abhi.leximentor.inventory.constants.DrillTypes;
import com.abhi.leximentor.inventory.constants.Status;
import com.abhi.leximentor.inventory.dto.drill.DrillChallengeScoresDTO;
import com.abhi.leximentor.inventory.dto.drill.DrillEvaluationDTO;
import com.abhi.leximentor.inventory.dto.drill.DrillReportResponseDTO;
import com.abhi.leximentor.inventory.entities.drill.DrillChallenge;
import com.abhi.leximentor.inventory.entities.drill.DrillChallengeScores;
import com.abhi.leximentor.inventory.entities.drill.DrillEvaluation;
import com.abhi.leximentor.inventory.entities.inv.Evaluator;
import com.abhi.leximentor.inventory.exceptions.entities.ServerException;
import com.abhi.leximentor.inventory.repository.drill.DrillChallengeRepository;
import com.abhi.leximentor.inventory.repository.drill.DrillChallengeScoreRepository;
import com.abhi.leximentor.inventory.repository.drill.DrillEvaluationRepository;
import com.abhi.leximentor.inventory.repository.inv.EvaluatorRepository;
import com.abhi.leximentor.inventory.service.drill.DrillEvaluationService;
import com.abhi.leximentor.inventory.service.drill.impl.strategy.DrillEvaluationStrategy;
import com.abhi.leximentor.inventory.service.drill.impl.strategy.DrillEvaluationStrategyRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class DrillEvaluationServiceImpl implements DrillEvaluationService {
    private final DrillEvaluationRepository drillEvaluationRepository;
    private final EvaluatorRepository evaluatorRepository;
    private final DrillChallengeScoreRepository drillChallengeScoreRepository;
    private final DrillChallengeRepository drillChallengeRepository;
    private final DrillEvaluationStrategyRegistry drillEvaluationStrategyRegistry;

    @Override
    @Transactional
    public DrillEvaluationDTO add(DrillEvaluationDTO dto) {
        log.info("Adding drill evaluation. scoreRefId={}", dto == null || dto.getDrillChallengeScoresDTO() == null ? null : dto.getDrillChallengeScoresDTO().getRefId());
        DrillChallengeScores drillChallengeScores = drillChallengeScoreRepository.findByRefId(Long.parseLong(dto.getDrillChallengeScoresDTO().getRefId()));
        Evaluator evaluator = evaluatorRepository.findByNameAndDrillType(dto.getEvaluator(), drillChallengeScores.getChallengeId().getDrillType());
        DrillEvaluation drillEvaluation = DrillServiceUtil.DrillEvaluationUtil.buildEntity(dto, evaluator, drillChallengeScores);
        drillEvaluation = drillEvaluationRepository.save(drillEvaluation);
        DrillEvaluationDTO response = DrillServiceUtil.DrillEvaluationUtil.buildDTO(drillEvaluation, DrillServiceUtil.DrillChallengeScoreUtil.buildDTO(drillEvaluation.getDrillChallengeScores()));
        log.info("Added drill evaluation. evalRefId={}", response.getRefId());
        return response;
    }

    @Override
    @Transactional
    public List<DrillEvaluationDTO> addAll(List<DrillEvaluationDTO> dtos) {
        log.info("Adding drill evaluations. count={}", dtos == null ? 0 : dtos.size());
        List<DrillEvaluationDTO> response = dtos.stream().map(this::add).collect(Collectors.toList());
        log.info("Added drill evaluations. count={}", response.size());
        return response;
    }

    @Override
    public DrillReportResponseDTO getEvaluationReport(long challengeRefId) {
        log.info("Fetching evaluation report. challengeRefId={}", challengeRefId);
        DrillChallenge challenge = drillChallengeRepository.findByRefId(challengeRefId);
        List<DrillChallengeScoresDTO> drillChallengeScoresDTOS = challenge.getDrillChallengeScoresList().stream().map(DrillServiceUtil.DrillChallengeScoreUtil::buildDTO).toList();
        List<DrillEvaluationDTO> drillEvaluationDTOS = drillEvaluationRepository.findByDrillChallengeScoresIn(challenge.getDrillChallengeScoresList()).stream().map(evaluation -> DrillServiceUtil.DrillEvaluationUtil.buildDTO(evaluation, DrillServiceUtil.DrillChallengeScoreUtil.buildDTO(evaluation.getDrillChallengeScores()))).toList();
        DrillReportResponseDTO response = DrillReportResponseDTO.builder().challengeRefId(String.valueOf(challenge.getRefId())).evaluator(drillEvaluationDTOS.get(0).getEvaluator()).drillType(challenge.getDrillType()).drillEvaluationDTOS(drillEvaluationDTOS).totalCorrect(challenge.getTotalCorrect()).totalIncorrect(challenge.getTotalWrong()).score(challenge.getDrillScore()).isPassed(challenge.isPass()).build();
        log.info("Fetched evaluation report. challengeRefId={}, evaluations={}", challengeRefId, drillEvaluationDTOS.size());
        return response;
    }
    @Override
    @Transactional
    public List<DrillEvaluationDTO> evaluate(List<DrillChallengeScoresDTO> drillChallengeScoresDTOS, String evaluator, long challengeRefId) {
        log.info("Evaluation requested. challengeRefId={}, evaluator={}, scoresCount={}", challengeRefId, evaluator, drillChallengeScoresDTOS == null ? 0 : drillChallengeScoresDTOS.size());
        DrillChallenge challenge = drillChallengeRepository.findByRefId(challengeRefId);
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            challenge.setEvaluationStatus(Status.DrillChallenge.IN_PROGRESS);
            drillChallengeRepository.save(challenge);
        });
        try {
            List<DrillEvaluationDTO> drillEvaluationDTOS;
            DrillTypes drillType;
            try {
                drillType = DrillTypes.valueOf(challenge.getDrillType());
            } catch (Exception ex) {
                drillType = DrillTypes.LEARN_MEANING;
            }

            DrillEvaluationStrategy strategy = drillEvaluationStrategyRegistry.getStrategyOrDefault(drillType, DrillTypes.LEARN_MEANING);

            drillEvaluationDTOS = strategy.evaluate(drillChallengeScoresDTOS, challenge, evaluator);
            challenge.setEvaluationStatus(Status.DrillChallenge.COMPLETED);
            drillChallengeRepository.save(challenge);
            log.info("Evaluation completed. challengeRefId={}, evaluations={}", challengeRefId, drillEvaluationDTOS.size());
            return drillEvaluationDTOS;
        } catch (Exception ex) {
            challenge.setEvaluationStatus(Status.DrillChallenge.NOT_INITIATED);
            drillChallengeRepository.save(challenge);
            log.error("The evaluation is failed for some internal reasons {}", ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException(new ServerException().new InternalError(ex.getMessage()));
        }
    }
}
