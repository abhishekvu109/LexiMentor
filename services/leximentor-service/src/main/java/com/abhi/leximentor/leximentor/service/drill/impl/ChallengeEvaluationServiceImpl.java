package com.abhi.leximentor.leximentor.service.drill.impl;

import com.abhi.leximentor.leximentor.constants.ChallengeType;
import com.abhi.leximentor.leximentor.constants.Status;
import com.abhi.leximentor.leximentor.dto.drill.ChallengeScoresDTO;
import com.abhi.leximentor.leximentor.dto.drill.ChallengeEvaluationDTO;
import com.abhi.leximentor.leximentor.dto.drill.ChallengeReportResponseDTO;
import com.abhi.leximentor.leximentor.entities.drill.Challenge;
import com.abhi.leximentor.leximentor.entities.drill.ChallengeScores;
import com.abhi.leximentor.leximentor.entities.drill.ChallengeEvaluation;
import com.abhi.leximentor.leximentor.entities.inv.Evaluator;
import com.abhi.leximentor.leximentor.exceptions.entities.ServerException;
import com.abhi.leximentor.leximentor.mapper.DrillDomainMapper;
import com.abhi.leximentor.leximentor.repository.drill.ChallengeRepository;
import com.abhi.leximentor.leximentor.repository.drill.ChallengeScoreRepository;
import com.abhi.leximentor.leximentor.repository.drill.ChallengeEvaluationRepository;
import com.abhi.leximentor.leximentor.repository.inv.EvaluatorRepository;
import com.abhi.leximentor.leximentor.service.drill.ChallengeEvaluationService;
import com.abhi.leximentor.leximentor.service.drill.impl.strategy.DrillEvaluationStrategy;
import com.abhi.leximentor.leximentor.service.drill.impl.strategy.DrillEvaluationStrategyRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class ChallengeEvaluationServiceImpl implements ChallengeEvaluationService {
    private final ChallengeEvaluationRepository challengeEvaluationRepository;
    private final EvaluatorRepository evaluatorRepository;
    private final ChallengeScoreRepository challengeScoreRepository;
    private final ChallengeRepository challengeRepository;
    private final DrillEvaluationStrategyRegistry drillEvaluationStrategyRegistry;
    private final DrillDomainMapper drillDomainMapper;

    @Override
    @Transactional
    public ChallengeEvaluationDTO add(ChallengeEvaluationDTO dto) {
        log.info("Adding drill evaluation. scoreKey={}", dto == null || dto.getChallengeScoresDTO() == null ? null : dto.getChallengeScoresDTO().getKey());
        ChallengeScores challengeScores = challengeScoreRepository.findByKey(dto.getChallengeScoresDTO().getKey());
        Evaluator evaluator = evaluatorRepository.findByNameAndChallengeType(dto.getEvaluator(), challengeScores.getChallenge().getChallengeType());
        ChallengeEvaluation challengeEvaluation = drillDomainMapper.toEntity(dto, evaluator, challengeScores);
        challengeEvaluation = challengeEvaluationRepository.save(challengeEvaluation);
        ChallengeEvaluationDTO response = drillDomainMapper.toDto(challengeEvaluation, drillDomainMapper.toDto(challengeEvaluation.getChallengeScores()));
        log.info("Added drill evaluation. evalKey={}", response.getKey());
        return response;
    }

    @Override
    @Transactional
    public List<ChallengeEvaluationDTO> addAll(List<ChallengeEvaluationDTO> dtos) {
        log.info("Adding drill evaluations. count={}", dtos == null ? 0 : dtos.size());
        List<ChallengeEvaluationDTO> response = dtos.stream().map(this::add).collect(Collectors.toList());
        log.info("Added drill evaluations. count={}", response.size());
        return response;
    }

    @Override
    public ChallengeReportResponseDTO getEvaluationReport(String challengeKey) {
        log.info("Fetching evaluation report. challengeKey={}", challengeKey);
        Challenge challenge = challengeRepository.findByKey(challengeKey);
        List<ChallengeEvaluationDTO> challengeEvaluationDTOS = challengeEvaluationRepository.findByChallengeScoresIn(challenge.getChallengeScoresList())
                .stream()
                .map(evaluation -> drillDomainMapper.toDto(evaluation, drillDomainMapper.toDto(evaluation.getChallengeScores())))
                .toList();
        ChallengeReportResponseDTO response = ChallengeReportResponseDTO.builder()
                .challengeKey(challenge.getKey())
                .evaluator(challengeEvaluationDTOS.isEmpty() ? null : challengeEvaluationDTOS.get(0).getEvaluator())
                .challengeType(challenge.getChallengeType().name())
                .challengeEvaluationDTOS(challengeEvaluationDTOS)
                .totalCorrect(challenge.getTotalCorrect())
                .totalIncorrect(challenge.getTotalWrong())
                .score(challenge.getScore())
                .isPassed(challenge.isPass())
                .build();
        log.info("Fetched evaluation report. challengeKey={}, evaluations={}", challengeKey, challengeEvaluationDTOS.size());
        return response;
    }
    @Override
    @Transactional
    public List<ChallengeEvaluationDTO> evaluate(List<ChallengeScoresDTO> challengeScoresDTOS, String evaluator, String challengeKey) {
        log.info("Evaluation requested. challengeKey={}, evaluator={}, scoresCount={}", challengeKey, evaluator, challengeScoresDTOS == null ? 0 : challengeScoresDTOS.size());
        Challenge challenge = challengeRepository.findByKey(challengeKey);
        challenge.setEvaluationStatus(Status.DrillChallenge.IN_PROGRESS);
        challengeRepository.save(challenge);
        try {
            List<ChallengeEvaluationDTO> challengeEvaluationDTOS;
            ChallengeType drillType;
            try {
                drillType = challenge.getChallengeType();
            } catch (Exception ex) {
                drillType = ChallengeType.LEARN_MEANING;
            }

            DrillEvaluationStrategy strategy = drillEvaluationStrategyRegistry.getStrategyOrDefault(drillType, ChallengeType.LEARN_MEANING);

            challengeEvaluationDTOS = strategy.evaluate(challengeScoresDTOS, challenge, evaluator);
            challenge.setEvaluationStatus(Status.DrillChallenge.COMPLETED);
            challengeRepository.save(challenge);
            log.info("Evaluation completed. challengeKey={}, evaluations={}", challengeKey, challengeEvaluationDTOS.size());
            return challengeEvaluationDTOS;
        } catch (Exception ex) {
            challenge.setEvaluationStatus(Status.DrillChallenge.NOT_INITIATED);
            challengeRepository.save(challenge);
            log.error("The evaluation is failed for some internal reasons {}", ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException(new ServerException().new InternalError(ex.getMessage()));
        }
    }
}
