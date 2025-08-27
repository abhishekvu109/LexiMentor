package com.abhi.leximentor.inventory.service.drill.impl;

import com.abhi.leximentor.inventory.constants.DrillTypes;
import com.abhi.leximentor.inventory.constants.Status;
import com.abhi.leximentor.inventory.dto.drill.*;
import com.abhi.leximentor.inventory.entities.drill.*;
import com.abhi.leximentor.inventory.entities.inv.Evaluator;
import com.abhi.leximentor.inventory.entities.inv.WordMetadata;
import com.abhi.leximentor.inventory.service.NamedObjectServiceImpl;
import com.abhi.leximentor.inventory.util.ApplicationUtil;
import com.abhi.leximentor.inventory.util.CollectionUtil;
import com.abhi.leximentor.inventory.util.KeyGeneratorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DrillServiceUtil {

    public static class DrillMetadataUtil {
        public static DrillMetadata buildEntity(List<WordMetadata> wordMetadataList, ApplicationUtil applicationUtil) {
            DrillMetadata drillMetadata = DrillMetadata.builder().name(applicationUtil.getDrillName()).refId(KeyGeneratorUtil.refId()).uuid(KeyGeneratorUtil.uuid()).status(Status.ApplicationStatus.ACTIVE).build();
            drillMetadata.setDrillSetList(wordMetadataList.stream().map(wordMetadata -> DrillSetUtil.buildEntity(wordMetadata, drillMetadata)).collect(Collectors.toList()));
            return drillMetadata;
        }

        public static DrillMetadataDTO buildDTO(DrillMetadata drillMetadata) {
            return DrillMetadataDTO.builder().refId(String.valueOf(drillMetadata.getRefId())).name(drillMetadata.getName()).status(Status.ApplicationStatus.getStatusStr(drillMetadata.getStatus())).crtnDate(drillMetadata.getCrtnDate()).overAllScore(drillMetadata.getOverallScore()).drillName(drillMetadata.getNamedObject() == null ? "" : drillMetadata.getNamedObject().getName()).namedObjectDTO((drillMetadata.getNamedObject() != null) ? NamedObjectServiceImpl.NamedObjectBuilder.buildDTO(drillMetadata.getNamedObject()) : null).build();
        }
    }

    public static class DrillSetUtil {
        public static DrillSet buildEntity(WordMetadata wordMetadata, DrillMetadata drillMetadata) {
            return DrillSet.builder().uuid(KeyGeneratorUtil.uuid()).refId(KeyGeneratorUtil.refId()).drillId(drillMetadata).wordId(wordMetadata).build();
        }

        public static DrillSetDTO buildDTO(DrillSet drillSet) {
            return DrillSetDTO.builder().refId(String.valueOf(drillSet.getRefId())).drillRefId(String.valueOf(drillSet.getDrillId().getRefId())).crtnDate(drillSet.getCrtndate()).wordRefId(String.valueOf(drillSet.getWordId().getRefId())).word(drillSet.getWordId().getWord()).build();
        }
    }

    public static class DrillChallengeUtil {
        public static DrillChallenge buildEntity(DrillMetadata drillMetadata, DrillTypes drillTypes) {
            DrillChallenge drillChallenge = DrillChallenge.builder().status(Status.DrillChallenge.NOT_INITIATED).evaluationStatus(Status.DrillChallenge.NOT_INITIATED).uuid(KeyGeneratorUtil.uuid()).drillType(drillTypes.name()).refId(KeyGeneratorUtil.refId()).drillId(drillMetadata).drillScore(0).isPass(false).totalCorrect(0).totalWrong(0).build();
            drillChallenge.setDrillChallengeScoresList(CollectionUtil.isNotEmpty(drillMetadata.getDrillSetList()) ? drillMetadata.getDrillSetList().stream().map(d -> DrillChallengeScoreUtil.buildEntity(drillChallenge, d, drillTypes)).collect(Collectors.toList()) : null);
            return drillChallenge;
        }

        public static DrillChallengeDTO buildDTO(DrillChallenge entity) {
            DrillMetadata drillMetadata = entity.getDrillId();
            return DrillChallengeDTO.builder().refId(String.valueOf(entity.getRefId())).drillType(entity.getDrillType()).evaluationStatus(Status.DrillChallenge.getEvaluationStatus(entity.getEvaluationStatus())).status(Status.DrillChallenge.getStatus(entity.getStatus())).drillRefId(String.valueOf(drillMetadata.getRefId())).drillScore(entity.getDrillScore()).isPass(entity.isPass()).totalCorrect(entity.getTotalCorrect()).totalWrong(entity.getTotalWrong()).crtnDate(entity.getCrtnDate()).build();
        }

        public static synchronized boolean isPass(double score) {
            return score > 70;
        }

        public static synchronized double score(int totalCorrect, int totalIncorrect) {
            int totalQuestions = totalCorrect + totalIncorrect;
            return ((double) totalCorrect / totalQuestions) * 100.00;
        }
    }

    public static class DrillChallengeScoreUtil {
        public static DrillChallengeScores buildEntity(DrillChallenge drillChallenge, DrillSet drillSet, DrillTypes drillTypes) {
            String question = (Objects.requireNonNull(drillTypes) == DrillTypes.GUESS_WORD) ? drillSet.getWordId().getMeanings().get(0).getDefinition() : drillSet.getWordId().getWord();
            return DrillChallengeScores.builder().uuid(KeyGeneratorUtil.uuid()).refId(KeyGeneratorUtil.refId()).challengeId(drillChallenge).drillSetId(drillSet).question(question).build();
        }

        public static DrillChallengeScoresDTO buildDTO(DrillChallengeScores drillChallengeScores) {
            return DrillChallengeScoresDTO.builder().refId(String.valueOf(drillChallengeScores.getRefId())).drillChallengeRefId(String.valueOf(drillChallengeScores.getChallengeId().getRefId())).drillSetRefId(String.valueOf(drillChallengeScores.getDrillSetId().getRefId())).isCorrect(drillChallengeScores.isCorrect()).response(drillChallengeScores.getResponse()).crtnDate(drillChallengeScores.getCrtnDate()).question(drillChallengeScores.getQuestion()).description(drillChallengeScores.getDescription()).build();
        }

    }

    public static class DrillEvaluationUtil {
        public static DrillEvaluation buildEntity(DrillEvaluationDTO dto, Evaluator evaluator, DrillChallengeScores drillChallengeScores) {
            return DrillEvaluation.builder().uuid(KeyGeneratorUtil.uuid()).refId(KeyGeneratorUtil.refId()).evaluator(evaluator).confidence(dto.getConfidence()).reason(dto.getReason()).evaluationTime(dto.getEvaluationTime()).drillChallengeScores(drillChallengeScores).build();
        }

        public static DrillEvaluationDTO buildDTO(DrillEvaluation evaluation, DrillChallengeScoresDTO drillChallengeScoresDTO) {
            return DrillEvaluationDTO.builder().refId(evaluation.getRefId()).drillChallengeScoresDTO(drillChallengeScoresDTO).evaluator(evaluation.getEvaluator().getName()).confidence(evaluation.getConfidence()).reason(evaluation.getReason()).evaluationTime(evaluation.getEvaluationTime()).build();
        }
    }
}
