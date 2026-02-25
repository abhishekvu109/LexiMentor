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
import com.abhi.leximentor.leximentor.repository.drill.ChallengeRepository;
import com.abhi.leximentor.leximentor.repository.drill.ChallengeScoreRepository;
import com.abhi.leximentor.leximentor.repository.drill.ChallengeEvaluationRepository;
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
    private final ChallengeScoreRepository challengeScoreRepository;
    private final ChallengeRepository challengeRepository;
    private final EvaluatorRepository evaluatorRepository;
    private final ChallengeEvaluationRepository challengeEvaluationRepository;
    @Autowired
    protected DrillDomainMapper drillDomainMapper;

    protected abstract boolean isCorrect(ChallengeScoresDTO dto, ChallengeScores scores, DrillSet drillSet, WordMetadata wordMetadata);

    protected boolean requiresWordMetadata() {
        return true;
    }

    @Override
    public List<ChallengeEvaluationDTO> evaluate(List<ChallengeScoresDTO> ChallengeScoresDTOS, Challenge challenge, String evaluatorName) {
        log.info("Initiated the {} evaluation.", getType().name());
        List<ChallengeEvaluationDTO> challengeEvaluationDTOS = new LinkedList<>();
        List<ChallengeScores> challengeScores = new LinkedList<>();
        Map<String, ChallengeScores> scoreRefMap = new HashMap<>();
        int totalCorrect = 0;
        int totalIncorrect = 0;

        Evaluator evaluator = evaluatorRepository.findByChallengeType(getType()).stream().findFirst()
                .orElseThrow(() -> new ServerException().new InternalError("No evaluator found for drill type: " + getType().name()));

        for (ChallengeScoresDTO dto : ChallengeScoresDTOS) {
            ChallengeScores scores = challengeScoreRepository.findByKey(dto.getKey());
            DrillSet drillSet = null;
            WordMetadata wordMetadata = null;
            if (requiresWordMetadata()) {
                drillSet = drillSetRepository.findByKey(dto.getDrillSetKey()).orElse(null);
                wordMetadata = drillSet.getWord();
            }

            boolean correct = isCorrect(dto, scores, drillSet, wordMetadata);
            scores.setCorrect(correct);
            totalCorrect += correct ? 1 : 0;
            totalIncorrect += correct ? 0 : 1;
            challengeScores.add(scores);
            scoreRefMap.put(dto.getKey(), scores);
            challengeEvaluationDTOS.add(ChallengeEvaluationDTO.builder()
                    .challengeScoresDTO(dto)
                    .reason(correct ? "The match is found." : "The match is not found.")
                    .confidence(100)
                    .evaluator(evaluator.getName())
                    .build());
        }

        challengeScoreRepository.saveAll(challengeScores);
        challenge.setTotalCorrect(totalCorrect);
        challenge.setTotalWrong(totalIncorrect);
        challenge.setStatus(Status.DrillChallenge.EVALUATED);
        challenge.setEvaluationStatus(Status.DrillChallenge.COMPLETED);
        challenge.setScore(drillDomainMapper.score(totalCorrect, totalIncorrect));
        challenge.setPass(drillDomainMapper.isPass(challenge.getScore()));
        challengeRepository.save(challenge);

        return persistEvaluations(challengeEvaluationDTOS, evaluator, scoreRefMap);
    }

    private List<ChallengeEvaluationDTO> persistEvaluations(List<ChallengeEvaluationDTO> dtos, Evaluator evaluator, Map<String, ChallengeScores> scoreRefMap) {
        List<ChallengeEvaluationDTO> saved = new LinkedList<>();
        for (ChallengeEvaluationDTO dto : dtos) {
            ChallengeScores scores = scoreRefMap.get(dto.getChallengeScoresDTO().getKey());
            ChallengeEvaluation challengeEvaluation = this.getDrillEvaluation(dto, scores, evaluator);
            ChallengeEvaluation stored = challengeEvaluationRepository.save(challengeEvaluation);
            saved.add(drillDomainMapper.toDto(stored, drillDomainMapper.toDto(stored.getChallengeScores())));
        }
        return saved;
    }

    private ChallengeEvaluation getDrillEvaluation(ChallengeEvaluationDTO dto, ChallengeScores challengeScores, Evaluator evaluator) {
        List<ChallengeEvaluation> challengeEvaluations = challengeEvaluationRepository.findByChallengeScoresIn(List.of(challengeScores));
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
