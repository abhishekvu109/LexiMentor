package com.abhi.leximentor.leximentor.mapper;

import com.abhi.leximentor.leximentor.constants.DrillTypes;
import com.abhi.leximentor.leximentor.constants.Status;
import com.abhi.leximentor.leximentor.dto.drill.*;
import com.abhi.leximentor.leximentor.entities.drill.*;
import com.abhi.leximentor.leximentor.entities.inv.Evaluator;
import com.abhi.leximentor.leximentor.entities.inv.WordMetadata;
import com.abhi.leximentor.leximentor.util.ApplicationUtil;
import com.abhi.leximentor.leximentor.util.CollectionUtil;
import com.abhi.leximentor.leximentor.util.KeyGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DrillDomainMapper {
    private final NamedObjectMapper namedObjectMapper;

    public DrillMetadata toEntity(List<WordMetadata> wordMetadataList, ApplicationUtil applicationUtil) {
        DrillMetadata drillMetadata = DrillMetadata.builder().name(applicationUtil.getDrillName()).refId(KeyGeneratorUtil.refId()).uuid(KeyGeneratorUtil.uuid()).status(Status.ApplicationStatus.ACTIVE).build();
        drillMetadata.setDrillSetList(wordMetadataList.stream().map(wordMetadata -> toEntity(wordMetadata, drillMetadata)).collect(Collectors.toList()));
        return drillMetadata;
    }

    public DrillMetadataDTO toDto(DrillMetadata drillMetadata) {
        return DrillMetadataDTO.builder().refId(String.valueOf(drillMetadata.getRefId())).name(drillMetadata.getName()).status(Status.ApplicationStatus.getStatusStr(drillMetadata.getStatus())).crtnDate(drillMetadata.getCrtnDate()).overAllScore(drillMetadata.getOverallScore()).drillName(drillMetadata.getNamedObject() == null ? "" : drillMetadata.getNamedObject().getName()).namedObjectDTO((drillMetadata.getNamedObject() != null) ? namedObjectMapper.toDto(drillMetadata.getNamedObject()) : null).build();
    }

    public DrillSet toEntity(WordMetadata wordMetadata, DrillMetadata drillMetadata) {
        return DrillSet.builder().uuid(KeyGeneratorUtil.uuid()).refId(KeyGeneratorUtil.refId()).drillId(drillMetadata).wordId(wordMetadata).build();
    }

    public DrillSetDTO toDto(DrillSet drillSet) {
        return DrillSetDTO.builder().refId(String.valueOf(drillSet.getRefId())).drillRefId(String.valueOf(drillSet.getDrillId().getRefId())).crtnDate(drillSet.getCrtndate()).wordRefId(String.valueOf(drillSet.getWordId().getRefId())).word(drillSet.getWordId().getWord()).build();
    }

    public Challenge toEntity(DrillMetadata drillMetadata, DrillTypes drillTypes) {
        Challenge challenge = Challenge.builder().status(Status.DrillChallenge.NOT_INITIATED).evaluationStatus(Status.DrillChallenge.NOT_INITIATED).uuid(KeyGeneratorUtil.uuid()).challengeType(drillTypes.name()).refId(KeyGeneratorUtil.refId()).drillId(drillMetadata).score(0).isPass(false).totalCorrect(0).totalWrong(0).build();
        challenge.setChallengeScoresList(CollectionUtil.isNotEmpty(drillMetadata.getDrillSetList()) ? drillMetadata.getDrillSetList().stream().map(d -> toEntity(challenge, d, drillTypes)).collect(Collectors.toList()) : null);
        return challenge;
    }

    public Challenge toEntity(DrillMetadata drillMetadata, DrillTypes drillTypes, String username) {
        Challenge challenge = Challenge.builder().status(Status.DrillChallenge.NOT_INITIATED).username(username).evaluationStatus(Status.DrillChallenge.NOT_INITIATED).uuid(KeyGeneratorUtil.uuid()).challengeType(drillTypes.name()).refId(KeyGeneratorUtil.refId()).drillId(drillMetadata).score(0).isPass(false).totalCorrect(0).totalWrong(0).build();
        challenge.setChallengeScoresList(CollectionUtil.isNotEmpty(drillMetadata.getDrillSetList()) ? drillMetadata.getDrillSetList().stream().map(d -> toEntity(challenge, d, drillTypes)).collect(Collectors.toList()) : null);
        return challenge;
    }

    public ChallengeDTO toDto(Challenge entity) {
        DrillMetadata drillMetadata = entity.getDrillId();
        return ChallengeDTO.builder().username(entity.getUsername()).refId(String.valueOf(entity.getRefId())).drillType(entity.getChallengeType()).evaluationStatus(Status.DrillChallenge.getEvaluationStatus(entity.getEvaluationStatus())).status(Status.DrillChallenge.getStatus(entity.getStatus())).drillRefId(String.valueOf(drillMetadata.getRefId())).drillScore(entity.getScore()).isPass(entity.isPass()).totalCorrect(entity.getTotalCorrect()).totalWrong(entity.getTotalWrong()).crtnDate(entity.getCrtnDate()).build();
    }

    public boolean isPass(double score) {
        return score > 70;
    }

    public double score(int totalCorrect, int totalIncorrect) {
        int totalQuestions = totalCorrect + totalIncorrect;
        return ((double) totalCorrect / totalQuestions) * 100.00;
    }

    public ChallengeScores toEntity(Challenge challenge, DrillSet drillSet, DrillTypes drillTypes) {
        String question = (Objects.requireNonNull(drillTypes) == DrillTypes.GUESS_WORD) ? drillSet.getWordId().getMeanings().get(0).getDefinition() : drillSet.getWordId().getWord();
        return ChallengeScores.builder().uuid(KeyGeneratorUtil.uuid()).refId(KeyGeneratorUtil.refId()).challengeId(challenge).drillSetId(drillSet).question(question).build();
    }

    public ChallengeScoresDTO toDto(ChallengeScores challengeScores) {
        return ChallengeScoresDTO.builder().refId(String.valueOf(challengeScores.getRefId())).drillChallengeRefId(String.valueOf(challengeScores.getChallengeId().getRefId())).drillSetRefId(String.valueOf(challengeScores.getDrillSetId().getRefId())).isCorrect(challengeScores.isCorrect()).response(challengeScores.getResponse()).crtnDate(challengeScores.getCrtnDate()).question(challengeScores.getQuestion()).description(challengeScores.getDescription()).build();
    }

    public ChallengeEvaluation toEntity(ChallengeEvaluationDTO dto, Evaluator evaluator, ChallengeScores challengeScores) {
        return ChallengeEvaluation.builder().uuid(KeyGeneratorUtil.uuid()).refId(KeyGeneratorUtil.refId()).evaluator(evaluator).confidence(dto.getConfidence()).reason(dto.getReason()).evaluationTime(dto.getEvaluationTime()).challengeScores(challengeScores).build();
    }

    public ChallengeEvaluationDTO toDto(ChallengeEvaluation evaluation, ChallengeScoresDTO ChallengeScoresDTO) {
        return ChallengeEvaluationDTO.builder().refId(evaluation.getRefId()).ChallengeScoresDTO(ChallengeScoresDTO).evaluator(evaluation.getEvaluator().getName()).confidence(evaluation.getConfidence()).reason(evaluation.getReason()).evaluationTime(evaluation.getEvaluationTime()).build();
    }
}
