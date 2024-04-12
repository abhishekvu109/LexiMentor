package com.abhi.leximentor.fitmate.service.impl;


import com.abhi.leximentor.fitmate.constants.Status;
import com.abhi.leximentor.fitmate.dto.BodyPartsDTO;
import com.abhi.leximentor.fitmate.dto.ExerciseDTO;
import com.abhi.leximentor.fitmate.dto.RoutineDTO;
import com.abhi.leximentor.fitmate.dto.TrainingMetadataDTO;
import com.abhi.leximentor.fitmate.entities.BodyParts;
import com.abhi.leximentor.fitmate.entities.Exercise;
import com.abhi.leximentor.fitmate.entities.Routine;
import com.abhi.leximentor.fitmate.entities.TrainingMetadata;
import com.abhi.leximentor.fitmate.util.KeyGeneratorUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FitmateServiceUtil {
    public static class TrainingMetadataUtil {
        public static TrainingMetadata buildEntity(TrainingMetadataDTO dto) {
            return TrainingMetadata.builder().uuid(KeyGeneratorUtil.uuid()).refId(KeyGeneratorUtil.refId()).name(dto.getName()).description(dto.getDescription()).status(Status.ApplicationStatus.getStatus(dto.getStatus())).build();
        }

        public static TrainingMetadataDTO buildDto(TrainingMetadata entity) {
            return TrainingMetadataDTO.builder().refId(String.valueOf(entity.getRefId())).name(entity.getName()).description(entity.getDescription()).status(Status.ApplicationStatus.getStatusStr(entity.getStatus())).crtnDate(entity.getCrtnDate()).lastUpdDate(entity.getLastUpdDate()).build();
        }
    }

    public static class ExcerciseUtil {
        public static Exercise buildEntity(ExerciseDTO dto, TrainingMetadata trainingMetadata, BodyParts bodyParts, List<BodyParts> secondaryBodyParts) {
            return Exercise.builder().uuid(KeyGeneratorUtil.uuid()).refId(KeyGeneratorUtil.refId()).name(dto.getName()).description(dto.getDescription()).unit(dto.getUnit()).status(Status.ApplicationStatus.getStatus(dto.getStatus())).trainingMetadata(trainingMetadata).targetBodyPart(bodyParts).secondaryBodyParts(secondaryBodyParts).build();
        }

        public static ExerciseDTO buildDto(Exercise entity) {
            return ExerciseDTO.builder().refId(String.valueOf(entity.getRefId())).name(entity.getName()).description(entity.getDescription()).unit(entity.getUnit()).status(Status.ApplicationStatus.getStatusStr(entity.getStatus())).crtnDate(entity.getCrtnDate()).lastUpdDate(entity.getLastUpdDate()).trainingMetadata(TrainingMetadataUtil.buildDto(entity.getTrainingMetadata())).targetBodyPart(BodyPartsUtil.buildDto(entity.getTargetBodyPart())).secondaryBodyParts(CollectionUtils.isNotEmpty(entity.getSecondaryBodyParts()) ? entity.getSecondaryBodyParts().stream().map(BodyParts::getName).toList() : null).build();
        }
    }

    public static class BodyPartsUtil {
        public static BodyParts buildEntity(BodyPartsDTO dto) {
            return BodyParts.builder().refId(KeyGeneratorUtil.refId()).primaryName(dto.getPrimaryName()).description(dto.getDescription()).uuid(KeyGeneratorUtil.uuid()).name(dto.getName()).status(Status.ApplicationStatus.getStatus(dto.getStatus())).build();
        }

        public static BodyPartsDTO buildDto(BodyParts entity) {
            return BodyPartsDTO.builder().refId(String.valueOf(entity.getRefId())).primaryName(entity.getPrimaryName()).description(entity.getDescription()).name(entity.getName()).status(Status.ApplicationStatus.getStatusStr(entity.getStatus())).build();
        }
    }


    public static class RoutineUtil {
        public static Routine buildEntity(RoutineDTO dto, Exercise excercise) {
            return Routine.builder().uuid(KeyGeneratorUtil.uuid()).refId(KeyGeneratorUtil.refId()).description(dto.getDescription()).isCompleted(dto.isCompleted()).completionUnit(dto.getCompletionUnit()).measurement(dto.getMeasurement()).excercise(excercise).build();
        }

        public static RoutineDTO buildDto(Routine entity) {
            return RoutineDTO.builder().refId(String.valueOf(entity.getRefId())).description(entity.getDescription()).isCompleted(entity.isCompleted()).completionUnit(entity.getCompletionUnit()).measurement(entity.getMeasurement()).crtnDate(entity.getCrtndate()).lastUpdDate(entity.getLastUpdDate()).excerciseDTO(ExcerciseUtil.buildDto(entity.getExcercise())).build();
        }
    }
}
