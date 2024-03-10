package com.abhi.leximentor.inventory.service.drill.impl;

import com.abhi.leximentor.inventory.constants.DrillTypes;
import com.abhi.leximentor.inventory.constants.Status;
import com.abhi.leximentor.inventory.dto.drill.DrillChallengeDTO;
import com.abhi.leximentor.inventory.dto.drill.DrillChallengeScoresDTO;
import com.abhi.leximentor.inventory.dto.drill.DrillMetadataDTO;
import com.abhi.leximentor.inventory.dto.drill.DrillSetDTO;
import com.abhi.leximentor.inventory.entities.drill.DrillChallenge;
import com.abhi.leximentor.inventory.entities.drill.DrillChallengeScores;
import com.abhi.leximentor.inventory.entities.drill.DrillMetadata;
import com.abhi.leximentor.inventory.entities.drill.DrillSet;
import com.abhi.leximentor.inventory.entities.inv.WordMetadata;
import com.abhi.leximentor.inventory.util.ApplicationUtil;
import com.abhi.leximentor.inventory.util.CollectionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DrillServiceUtil {

    public static class DrillMetadataUtil {
        public static DrillMetadata buildEntity(int size, List<WordMetadata> wordMetadataList, ApplicationUtil applicationUtil) {
            DrillMetadata drillMetadata = DrillMetadata.builder().name(applicationUtil.generateRandomString(size)).refId(UUID.randomUUID().getMostSignificantBits()).uuid(UUID.randomUUID().toString()).status(Status.ACTIVE).build();
            drillMetadata.setDrillSetList(wordMetadataList.stream().map(wordMetadata -> DrillSetUtil.buildEntity(wordMetadata, drillMetadata)).collect(Collectors.toList()));
            return drillMetadata;
        }

        public static DrillMetadataDTO buildDTO(DrillMetadata drillMetadata) {
            DrillMetadataDTO drillMetadataDTO = DrillMetadataDTO.builder().refId(drillMetadata.getRefId()).name(drillMetadata.getName()).status(Status.getStatus(drillMetadata.getStatus())).crtnDate(drillMetadata.getCrtnDate()).overAllScore(drillMetadata.getOverallScore()).build();
//            drillMetadataDTO.setDrillSetDTOList(CollectionUtil.isNotEmpty(drillMetadata.getDrillSetList()) ? drillMetadata.getDrillSetList().stream().map(d -> DrillSetUtil.buildDTO(d, drillMetadata)).collect(Collectors.toList()) : null);
//            drillMetadataDTO.setDrillChallengeDTOList(CollectionUtil.isNotEmpty(drillMetadata.getDrillChallenges()) ? drillMetadata.getDrillChallenges().stream().map(d -> DrillChallengeUtil.buildDTO(d, drillMetadata)).collect(Collectors.toList()) : null);
            return drillMetadataDTO;
        }
    }

    public static class DrillSetUtil {
        public static DrillSet buildEntity(WordMetadata wordMetadata, DrillMetadata drillMetadata) {
            return DrillSet.builder().uuid(UUID.randomUUID().toString()).refId(UUID.randomUUID().getMostSignificantBits()).drillId(drillMetadata).wordId(wordMetadata).build();
        }

        public static DrillSetDTO buildDTO(DrillSet drillSet) {
            return DrillSetDTO.builder().refId(drillSet.getRefId()).drillRefId(drillSet.getDrillId().getRefId()).crtnDate(drillSet.getCrtndate()).wordId(drillSet.getWordId().getRefId()).word(drillSet.getWordId().getWord()).build();
        }
    }

    public static class DrillChallengeUtil {
        public static DrillChallenge buildEntity(DrillMetadata drillMetadata, DrillTypes drillTypes) {
            DrillChallenge drillChallenge = DrillChallenge.builder().uuid(UUID.randomUUID().toString()).drillType(drillTypes.name()).refId(UUID.randomUUID().getMostSignificantBits()).drillId(drillMetadata).drillScore(0).isPass(false).totalCorrect(0).totalWrong(0).build();
            drillChallenge.setDrillChallengeScoresList(CollectionUtil.isNotEmpty(drillMetadata.getDrillSetList()) ? drillMetadata.getDrillSetList().stream().map(d -> DrillChallengeScoreUtil.buildEntity(drillChallenge, d)).collect(Collectors.toList()) : null);
            return drillChallenge;
        }

        public static DrillChallengeDTO buildDTO(DrillChallenge entity, DrillMetadata drillMetadata) {
            DrillChallengeDTO drillChallengeDTO = DrillChallengeDTO.builder().refId(entity.getRefId()).drillType(entity.getDrillType()).drillRefId(drillMetadata.getRefId()).drillScore(entity.getDrillScore()).isPass(entity.isPass()).totalCorrect(entity.getTotalCorrect()).totalWrong(entity.getTotalWrong()).crtnDate(entity.getCrtnDate()).build();
//            drillChallengeDTO.setDrillChallengeScoresDTOList(CollectionUtil.isNotEmpty(entity.getDrillChallengeScoresList()) ? entity.getDrillChallengeScoresList().stream().map(DrillChallengeScoreUtil::buildDTO).collect(Collectors.toList()) : null);
            return drillChallengeDTO;
        }
    }

    public static class DrillChallengeScoreUtil {
        public static DrillChallengeScores buildEntity(DrillChallenge drillChallenge, DrillSet drillSet) {
            return DrillChallengeScores.builder().uuid(UUID.randomUUID().toString()).refId(UUID.randomUUID().getMostSignificantBits()).challengeId(drillChallenge).drillSetId(drillSet).build();
        }

        public static DrillChallengeScoresDTO buildDTO(DrillChallengeScores drillChallengeScores) {
            return DrillChallengeScoresDTO.builder().refId(drillChallengeScores.getRefId()).drillChallengeRefId(drillChallengeScores.getChallengeId().getRefId()).drillSetRefId(drillChallengeScores.getDrillSetId().getRefId()).isCorrect(drillChallengeScores.isCorrect()).response(drillChallengeScores.getResponse()).crtnDate(drillChallengeScores.getCrtnDate()).description(drillChallengeScores.getDescription()).build();
        }
    }
}
