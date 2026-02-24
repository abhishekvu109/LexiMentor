package com.abhi.leximentor.leximentor.mapper;

import com.abhi.leximentor.leximentor.constants.ChallengeType;
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

    public Drill toEntity(List<WordMetadata> wordMetadataList, ApplicationUtil applicationUtil) {
        Drill drill = Drill.builder().name(applicationUtil.getDrillName()).refId(KeyGeneratorUtil.refId()).uuid(KeyGeneratorUtil.uuid()).status(Status.ApplicationStatus.ACTIVE).build();
        drill.setDrillSetList(wordMetadataList.stream().map(wordMetadata -> toEntity(wordMetadata, drill)).collect(Collectors.toList()));
        return drill;
    }

    public DrillDTO toDto(Drill drill) {
        return DrillDTO.builder().refId(String.valueOf(drill.getRefId())).name(drill.getName()).status(Status.ApplicationStatus.getStatusStr(drill.getStatus())).crtnDate(drill.getCreatedAt()).overAllScore(drill.getOverallScore()).drillName(drill.getNamedObject() == null ? "" : drill.getNamedObject().getName()).namedObjectDTO((drill.getNamedObject() != null) ? namedObjectMapper.toDto(drill.getNamedObject()) : null).build();
    }

    public DrillSet toEntity(WordMetadata wordMetadata, Drill drill) {
        return DrillSet.builder().uuid(KeyGeneratorUtil.uuid()).refId(KeyGeneratorUtil.refId()).drillId(drill).wordId(wordMetadata).build();
    }

    public DrillSetDTO toDto(DrillSet drillSet) {
        return DrillSetDTO.builder().refId(String.valueOf(drillSet.getRefId())).drillRefId(String.valueOf(drillSet.getDrill().getRefId())).crtnDate(drillSet.getCreatedAt()).wordRefId(String.valueOf(drillSet.getWord().getRefId())).word(drillSet.getWord().getWord()).build();
    }

    public Challenge toEntity(Drill drill, ChallengeType challengeType) {
        Challenge challenge = Challenge.builder().status(Status.DrillChallenge.NOT_INITIATED).evaluationStatus(Status.DrillChallenge.NOT_INITIATED).uuid(KeyGeneratorUtil.uuid()).challengeType(challengeType.name()).refId(KeyGeneratorUtil.refId()).drillId(drill).score(0).isPass(false).totalCorrect(0).totalWrong(0).build();
        challenge.setChallengeScoresList(CollectionUtil.isNotEmpty(drill.getDrillSetList()) ? drill.getDrillSetList().stream().map(d -> toEntity(challenge, d, challengeType)).collect(Collectors.toList()) : null);
        return challenge;
    }

    public Challenge toEntity(Drill drill, ChallengeType challengeType, String username) {
        Challenge challenge = Challenge.builder().status(Status.DrillChallenge.NOT_INITIATED).username(username).evaluationStatus(Status.DrillChallenge.NOT_INITIATED).uuid(KeyGeneratorUtil.uuid()).challengeType(challengeType.name()).refId(KeyGeneratorUtil.refId()).drillId(drill).score(0).isPass(false).totalCorrect(0).totalWrong(0).build();
        challenge.setChallengeScoresList(CollectionUtil.isNotEmpty(drill.getDrillSetList()) ? drill.getDrillSetList().stream().map(d -> toEntity(challenge, d, challengeType)).collect(Collectors.toList()) : null);
        return challenge;
    }

    public ChallengeDTO toDto(Challenge entity) {
        Drill drill = entity.getDrill();
        return ChallengeDTO.builder().username(entity.getUsername()).refId(String.valueOf(entity.getRefId())).drillType(entity.getChallengeType()).evaluationStatus(Status.DrillChallenge.getEvaluationStatus(entity.getEvaluationStatus())).status(Status.DrillChallenge.getStatus(entity.getStatus())).drillRefId(String.valueOf(drill.getRefId())).drillScore(entity.getScore()).isPass(entity.isPass()).totalCorrect(entity.getTotalCorrect()).totalWrong(entity.getTotalWrong()).crtnDate(entity.getCreatedAt()).build();
    }

    public boolean isPass(double score) {
        return score > 70;
    }

    public double score(int totalCorrect, int totalIncorrect) {
        int totalQuestions = totalCorrect + totalIncorrect;
        return ((double) totalCorrect / totalQuestions) * 100.00;
    }

    public ChallengeScores toEntity(Challenge challenge, DrillSet drillSet, ChallengeType challengeType) {
        String question = (Objects.requireNonNull(challengeType) == ChallengeType.GUESS_WORD) ? drillSet.getWord().getMeanings().get(0).getDefinition() : drillSet.getWord().getWord();
        return ChallengeScores.builder().uuid(KeyGeneratorUtil.uuid()).refId(KeyGeneratorUtil.refId()).challengeId(challenge).drillSetId(drillSet).question(question).build();
    }

    public ChallengeScoresDTO toDto(ChallengeScores challengeScores) {
        return ChallengeScoresDTO.builder().refId(String.valueOf(challengeScores.getRefId())).drillChallengeRefId(String.valueOf(challengeScores.getChallenge().getRefId())).drillSetRefId(String.valueOf(challengeScores.getDrillSet().getRefId())).isCorrect(challengeScores.isCorrect()).response(challengeScores.getResponse()).crtnDate(challengeScores.getCreatedAt()).question(challengeScores.getQuestion()).description(challengeScores.getDescription()).build();
    }

    public ChallengeEvaluation toEntity(ChallengeEvaluationDTO dto, Evaluator evaluator, ChallengeScores challengeScores) {
        return ChallengeEvaluation.builder().uuid(KeyGeneratorUtil.uuid()).refId(KeyGeneratorUtil.refId()).evaluator(evaluator).confidence(dto.getConfidence()).reason(dto.getReason()).evaluationTime(dto.getEvaluationTime()).challengeScores(challengeScores).build();
    }

    public ChallengeEvaluationDTO toDto(ChallengeEvaluation evaluation, ChallengeScoresDTO ChallengeScoresDTO) {
        return ChallengeEvaluationDTO.builder().refId(evaluation.getRefId()).ChallengeScoresDTO(ChallengeScoresDTO).evaluator(evaluation.getEvaluator().getName()).confidence(evaluation.getConfidence()).reason(evaluation.getReason()).evaluationTime(evaluation.getEvaluationTime()).build();
    }
}
