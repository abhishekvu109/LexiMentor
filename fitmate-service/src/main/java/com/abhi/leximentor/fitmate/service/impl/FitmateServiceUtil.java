package com.abhi.leximentor.fitmate.service.impl;


import com.abhi.leximentor.fitmate.constants.DifficultyLevel;
import com.abhi.leximentor.fitmate.constants.RiskLevel;
import com.abhi.leximentor.fitmate.constants.Status;
import com.abhi.leximentor.fitmate.constants.Unit;
import com.abhi.leximentor.fitmate.dto.*;
import com.abhi.leximentor.fitmate.entities.*;
import com.abhi.leximentor.fitmate.util.FitmateUtil;
import com.abhi.leximentor.fitmate.util.KeyGeneratorUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Component
public class FitmateServiceUtil {
    public static class TrainingMetadataUtil {
        public static Training buildEntity(TrainingDTO dto) {
            return Training.builder()
                    .uuid(KeyGeneratorUtil.uuid())
                    .refId(KeyGeneratorUtil.refId())
                    .name(dto.getName())
                    .description(dto.getDescription())
                    .status(Status.ApplicationStatus.getStatus(dto.getStatus()))
                    .build();
        }

        public static TrainingDTO buildDto(Training entity) {
            return TrainingDTO.builder()
                    .refId(String.valueOf(entity.getRefId()))
                    .name(entity.getName())
                    .description(entity.getDescription())
                    .status(Status.ApplicationStatus.getStatusStr(entity.getStatus()))
                    .crtnDate(entity.getCrtnDate())
                    .lastUpdDate(entity.getLastUpdDate())
                    .build();
        }
    }

    public static class ExerciseUtil {
        public static Exercise buildEntity(ExerciseDTO dto, Training training, BodyPart bodyPart, List<Muscle> muscles) {
            return Exercise.builder()
                    .uuid(KeyGeneratorUtil.uuid())
                    .refId(KeyGeneratorUtil.refId())
                    .name(dto.getName())
                    .description(dto.getDescription())
                    .unit(Unit.from(dto.getUnit()).getValue())
                    .status(Status.ApplicationStatus.getStatus(StringUtils.isNotEmpty(dto.getStatus()) ?
                            dto.getStatus() :
                            Status.ApplicationStatus.getStatusStr(Status.ApplicationStatus.ACTIVE)))
                    .training(training).targetBodyPart(bodyPart)
                    .targetBodyPart(bodyPart)
                    .targetMuscles(muscles)
                    .equipments(dto.getEquipments())
                    .difficultyLevel(DifficultyLevel.parse(dto.getDifficultyLevel()).getScore())
                    .riskLevel(RiskLevel.parse(dto.getRiskLevel()).getScore())
                    .build();
        }

        public static ExerciseDTO buildDto(Exercise entity) {
            return ExerciseDTO.builder()
                    .refId(String.valueOf(entity.getRefId()))
                    .name(entity.getName())
                    .description(entity.getDescription())
                    .unit(entity.getUnit())
                    .status(Status.ApplicationStatus.getStatusStr(entity.getStatus()))
                    .crtnDate(entity.getCrtnDate())
                    .lastUpdDate(entity.getLastUpdDate())
                    .training(TrainingMetadataUtil.buildDto(entity.getTraining()))
                    .bodyPart(BodyPartsUtil.buildDto(entity.getTargetBodyPart()))
                    .targetMuscles(CollectionUtils.isNotEmpty(entity.getTargetMuscles()) ?
                            entity.getTargetMuscles().stream().map(muscle -> MuscleUtil.buildDTO(muscle, entity.getTargetBodyPart())).toList()
                            : Collections.emptyList())
                    .equipments(entity.getEquipments())
                    .difficultyLevel(DifficultyLevel.parse(entity.getDifficultyLevel()).toString())
                    .riskLevel(RiskLevel.parse(entity.getRiskLevel()).toString())
                    .build();
        }

        public static ExerciseDTO buildDto(Exercise entity, List<Muscle> muscles) {
            return ExerciseDTO.builder()
                    .refId(String.valueOf(entity.getRefId()))
                    .name(entity.getName())
                    .description(entity.getDescription())
                    .unit(entity.getUnit())
                    .status(Status.ApplicationStatus.getStatusStr(entity.getStatus()))
                    .crtnDate(entity.getCrtnDate())
                    .lastUpdDate(entity.getLastUpdDate())
                    .training(TrainingMetadataUtil.buildDto(entity.getTraining()))
                    .bodyPart(BodyPartsUtil.buildDto(entity.getTargetBodyPart()))
                    .equipments(entity.getEquipments())
                    .difficultyLevel(DifficultyLevel.parse(entity.getDifficultyLevel()).toString())
                    .riskLevel(RiskLevel.parse(entity.getRiskLevel()).toString())
                    .targetMuscles(CollectionUtils.isNotEmpty(muscles) ? muscles.stream().map(muscle -> MuscleUtil.buildDTO(muscle, entity.getTargetBodyPart())).toList() : Collections.emptyList())
                    .build();
        }
    }

    public static class BodyPartsUtil {
        public static BodyPart buildEntity(BodyPartsDTO dto) {
            return BodyPart.builder()
                    .refId(KeyGeneratorUtil.refId())
                    .description(dto.getDescription())
                    .uuid(KeyGeneratorUtil.uuid())
                    .name(dto.getName())
                    .status(Status.ApplicationStatus.getStatus(dto.getStatus()))
                    .build();
        }

        public static BodyPartsDTO buildDto(BodyPart entity) {
            return BodyPartsDTO.builder()
                    .refId(String.valueOf(entity.getRefId()))
                    .description(entity.getDescription())
                    .name(entity.getName())
                    .status(Status.ApplicationStatus.getStatusStr(entity.getStatus()))
//                    .muscleDTOList(CollectionUtils.isNotEmpty(entity.getTargetMuscles()) ? entity.getTargetMuscles().stream().map(MuscleUtil::buildDTO).toList() : Collections.emptyList())
                    .build();
        }
    }


    public static class RoutineUtil {
        public static Routine buildEntity(RoutineDTO dto, Training training, List<Drill> drills) {
            return Routine.builder()
                    .uuid(KeyGeneratorUtil.uuid())
                    .refId(KeyGeneratorUtil.refId())
                    .routineDate(StringUtils.isNotEmpty(dto.getWorkoutDate()) ? FitmateUtil.getLocalDateFromString("MM/dd/yyyy", dto.getWorkoutDate()) : LocalDate.now())
                    .description(dto.getDescription())
                    .status(Status.RoutineStatus.NOT_STARTED)
                    .training(training)
                    .drills(drills)
                    .burntCalories(dto.getBurntCalories())
                    .username(dto.getUsername())
                    .durationInMinutes(dto.getDurationInMinutes())
                    .build();
        }

        public static Routine buildEntity(RoutineDTO dto, Training training) {
            return Routine.builder()
                    .uuid(KeyGeneratorUtil.uuid())
                    .routineDate(StringUtils.isNotEmpty(dto.getWorkoutDate()) ? FitmateUtil.getLocalDateFromString("yyyy-MM-dd", dto.getWorkoutDate()) : LocalDate.now())
                    .refId(KeyGeneratorUtil.refId())
                    .description(dto.getDescription())
                    .status(Status.RoutineStatus.NOT_STARTED)
                    .training(training)
                    .burntCalories(dto.getBurntCalories())
                    .username(dto.getUsername())
                    .durationInMinutes(dto.getDurationInMinutes())
                    .build();
        }

        public static RoutineDTO buildDto(Routine entity) {
            return RoutineDTO.builder()
                    .refId(String.valueOf(entity.getRefId()))
                    .description(entity.getDescription())
                    .status(Status.RoutineStatus.toString(entity.getStatus()))
                    .crtnDate(entity.getCrtnDate())
                    .training(TrainingMetadataUtil.buildDto(entity.getTraining()))
                    .burntCalories(entity.getBurntCalories())
                    .drills(CollectionUtils.isNotEmpty(entity.getDrills()) ?
                            entity.getDrills().stream().map(DrillUtil::buildDTO).toList()
                            : Collections.emptyList())
                    .durationInMinutes(entity.getDurationInMinutes())
                    .workoutDate(entity.getRoutineDate() == null ? "" : entity.getRoutineDate().toString())
                    .key(entity.getUuid())
                    .username(entity.getUsername())
                    .build();
        }

        public static RoutineDTO buildDto(Routine entity, List<DrillDTO> drills) {
            return RoutineDTO.builder()
                    .refId(String.valueOf(entity.getRefId()))
                    .description(entity.getDescription())
                    .status(Status.RoutineStatus.toString(entity.getStatus()))
                    .crtnDate(entity.getCrtnDate())
                    .training(TrainingMetadataUtil.buildDto(entity.getTraining()))
                    .burntCalories(entity.getBurntCalories())
                    .drills(drills)
                    .durationInMinutes(entity.getDurationInMinutes())
                    .workoutDate(entity.getRoutineDate() == null ? "" : entity.getRoutineDate().toString())
                    .key(entity.getUuid())
                    .username(entity.getUsername())
                    .build();
        }
    }

    public static class MuscleUtil {
        public static Muscle buildEntity(MuscleDTO dto, BodyPart bodyPart) {
            return Muscle.builder()
                    .refId(KeyGeneratorUtil.refId())
                    .uuid(KeyGeneratorUtil.uuid())
                    .name(dto.getName())
                    .description(dto.getDescription())
                    .bodyPart(bodyPart.getRefId())
                    .build();
        }

        public static MuscleDTO buildDTO(Muscle muscle, BodyPart bodyPart) {
            return MuscleDTO.builder()
                    .name(muscle.getName())
                    .refId(String.valueOf(muscle.getRefId()))
                    .description(muscle.getDescription())
                    .status(Status.ApplicationStatus.getStatusStr(muscle.getStatus()))
                    .bodyPart(BodyPartsUtil.buildDto(bodyPart))
                    .build();
        }

        public static MuscleDTO buildDTO(Muscle muscle, String bodyPart) {
            return MuscleDTO.builder()
                    .name(muscle.getName())
                    .refId(String.valueOf(muscle.getRefId()))
                    .description(muscle.getDescription())
                    .status(Status.ApplicationStatus.getStatusStr(muscle.getStatus()))
                    .bodyPart(BodyPartsDTO.builder().name(bodyPart).build())
                    .build();
        }

    }

    public static class DrillUtil {
        public static Drill buildEntity(DrillDTO dto) {
            return Drill.builder()
                    .refId(KeyGeneratorUtil.refId())
                    .uuid(KeyGeneratorUtil.uuid())
                    .unit(dto.getUnit())
                    .measurementUnit(dto.getMeasurementUnit())
                    .measurement(dto.getMeasurement())
                    .repetition(dto.getRepetition())
                    .burntCalories(dto.getBurntCalories())
                    .notes(dto.getNotes())
                    .build();
        }

        public static Drill buildEntity(DrillDTO dto, Exercise exercise, Routine routine) {
            return Drill.builder()
                    .refId(KeyGeneratorUtil.refId())
                    .uuid(KeyGeneratorUtil.uuid())
                    .unit(dto.getUnit())
                    .measurementUnit(dto.getMeasurementUnit())
                    .measurement(dto.getMeasurement())
                    .repetition(dto.getRepetition())
                    .burntCalories(dto.getBurntCalories())
                    .notes(dto.getNotes())
                    .exercise(exercise)
                    .routine(routine.getRefId())
                    .build();
        }

        public static DrillDTO buildDTO(Drill entity) {
            return DrillDTO.builder()
                    .refId(String.valueOf(entity.getRefId()))
                    .unit(entity.getUnit())
                    .exercise(ExerciseUtil.buildDto(entity.getExercise()))
                    .routine(String.valueOf(entity.getRoutine()))
                    .measurementUnit(entity.getMeasurementUnit())
                    .measurement(entity.getMeasurement())
                    .repetition(entity.getRepetition())
                    .burntCalories(entity.getBurntCalories())
                    .notes(entity.getNotes())
                    .creationDate(entity.getCrtnDate())
//                    .muscle(MuscleUtil.buildDTO(entity.getMuscle(), entity.getExercise().getTargetBodyPart().getName()))
                    .build();
        }
    }
}
