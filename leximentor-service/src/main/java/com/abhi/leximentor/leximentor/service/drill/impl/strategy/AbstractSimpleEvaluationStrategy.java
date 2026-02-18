package com.abhi.leximentor.leximentor.service.drill.impl.strategy;

import com.abhi.leximentor.leximentor.constants.Status;
import com.abhi.leximentor.leximentor.dto.drill.ChallengeScoresDTO;
import com.abhi.leximentor.leximentor.dto.drill.ChallengeEvaluationDTO;
import com.abhi.leximentor.leximentor.entities.drill.Challenge;
import com.abhi.leximentor.leximentor.entities.drill.ChallengeScores;
import com.abhi.leximentor.leximentor.entities.drill.ChallengeEvaluation;
import com.abhi.leximentor.leximentor.entities.drill.DrillSet;
import com.abhi.leximentor.leximentor.entities.inv.Evaluator;
import com.abhi.leximentor.leximentor.entities.inv.WordMetadata;
import com.abhi.leximentor.leximentor.exceptions.entities.ServerException;
import com.abhi.leximentor.leximentor.mapper.DrillDomainMapper;
import com.abhi.leximentor.leximentor.repository.drill.DrillChallengeRepository;
import com.abhi.leximentor.leximentor.repository.drill.DrillChallengeScoreRepository;
import com.abhi.leximentor.leximentor.repository.drill.DrillEvaluationRepository;
import com.abhi.leximentor.leximentor.repository.drill.DrillSetRepository;
import com.abhi.leximentor.leximentor.repository.inv.EvaluatorRepository;
import com.abhi.leximentor.leximentor.util.CollectionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

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
    @Autowired
    protected DrillDomainMapper drillDomainMapper;

    protected abstract boolean isCorrect(ChallengeScoresDTO dto, ChallengeScores scores, DrillSet drillSet, WordMetadata wordMetadata);

    protected boolean requiresWordMetadata() {
        return true;
    }

    @Override
    public List<ChallengeEvaluationDTO> evaluate(List<ChallengeScoresDTO> ChallengeScoresDTOS, Challenge challenge, String evaluatorName) {
        log.info("Initiated the {} evaluation.", getType().name());
        List<ChallengeEvaluationDTO> ChallengeEvaluationDTOS = new LinkedList<>();
        List<ChallengeScores> challengeScores = new LinkedList<>();
        Map<String, ChallengeScores> scoreRefMap = new HashMap<>();
        int totalCorrect = 0;
        int totalIncorrect = 0;

        Evaluator evaluator = evaluatorRepository.findByDrillType(getType().name()).stream().findFirst()
                .orElseThrow(() -> new ServerException().new InternalError("No evaluator found for drill type: " + getType().name()));

        for (ChallengeScoresDTO dto : ChallengeScoresDTOS) {
            ChallengeScores scores = drillChallengeScoreRepository.findByRefId(Long.parseLong(dto.getRefId()));
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
            challengeScores.add(scores);
            scoreRefMap.put(dto.getRefId(), scores);
            ChallengeEvaluationDTOS.add(ChallengeEvaluationDTO.builder()
                    .ChallengeScoresDTO(dto)
                    .reason(correct ? "The match is found." : "The match is not found.")
                    .confidence(100)
                    .evaluator(evaluator.getName())
                    .build());
        }

        drillChallengeScoreRepository.saveAll(challengeScores);
        challenge.setTotalCorrect(totalCorrect);
        challenge.setTotalWrong(totalIncorrect);
        challenge.setStatus(Status.DrillChallenge.EVALUATED);
        challenge.setEvaluationStatus(Status.DrillChallenge.COMPLETED);
        challenge.setScore(drillDomainMapper.score(totalCorrect, totalIncorrect));
        challenge.setPass(drillDomainMapper.isPass(challenge.getScore()));
        drillChallengeRepository.save(challenge);

        return persistEvaluations(ChallengeEvaluationDTOS, evaluator, scoreRefMap);
    }

    private List<ChallengeEvaluationDTO> persistEvaluations(List<ChallengeEvaluationDTO> dtos, Evaluator evaluator, Map<String, ChallengeScores> scoreRefMap) {
        List<ChallengeEvaluationDTO> saved = new LinkedList<>();
        for (ChallengeEvaluationDTO dto : dtos) {
            ChallengeScores scores = scoreRefMap.get(dto.getChallengeScoresDTO().getRefId());
            ChallengeEvaluation challengeEvaluation = this.getDrillEvaluation(dto, scores, evaluator);
            ChallengeEvaluation stored = drillEvaluationRepository.save(challengeEvaluation);
            saved.add(drillDomainMapper.toDto(stored, drillDomainMapper.toDto(stored.getChallengeScores())));
        }
        return saved;
    }

    private ChallengeEvaluation getDrillEvaluation(ChallengeEvaluationDTO dto, ChallengeScores challengeScores, Evaluator evaluator) {
        List<ChallengeEvaluation> challengeEvaluations = drillEvaluationRepository.findByDrillChallengeScoresIn(List.of(challengeScores));
        ChallengeEvaluation challengeEvaluation;
        if (CollectionUtil.isNotEmpty(challengeEvaluations)) {
            challengeEvaluation = challengeEvaluations.get(0);
            challengeEvaluation.setEvaluator(evaluator);
            challengeEvaluation.setEvaluationTime(dto.getEvaluationTime());
            challengeEvaluation.setConfidence(dto.getConfidence());
            challengeEvaluation.setChallengeScores(challengeScores);
            challengeEvaluation.setReason(dto.getReason());
            return challengeEvaluation;
        } else {
            return drillDomainMapper.toEntity(dto, evaluator, challengeScores);
        }
    }
}
