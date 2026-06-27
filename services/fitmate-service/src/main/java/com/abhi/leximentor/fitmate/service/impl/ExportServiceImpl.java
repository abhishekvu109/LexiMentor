package com.abhi.leximentor.fitmate.service.impl;

import com.abhi.leximentor.fitmate.constants.DifficultyLevel;
import com.abhi.leximentor.fitmate.constants.RiskLevel;
import com.abhi.leximentor.fitmate.constants.Status;
import com.abhi.leximentor.fitmate.dto.RoutineDrillExportDTO;
import com.abhi.leximentor.fitmate.entities.Drill;
import com.abhi.leximentor.fitmate.entities.Exercise;
import com.abhi.leximentor.fitmate.entities.Muscle;
import com.abhi.leximentor.fitmate.entities.Routine;
import com.abhi.leximentor.fitmate.repository.ExerciseRepository;
import com.abhi.leximentor.fitmate.repository.FoodEntryRepository;
import com.abhi.leximentor.fitmate.repository.RoutineRepository;
import com.abhi.leximentor.fitmate.service.ExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {

    private final ExerciseRepository exerciseRepository;
    private final RoutineRepository routineRepository;
    private final FoodEntryRepository foodEntryRepository;
    private final TransactionTemplate transactionTemplate;

    @Override
    public StreamingResponseBody exportExercises(String trainingRefId, String bodyPartRefId) {
        return outputStream -> transactionTemplate.execute(txStatus -> {
            try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream))) {
                writer.println("RefID,Name,Description,Unit,Status,TrainingType,BodyPart,TargetMuscles,Equipments,DifficultyLevel,RiskLevel");

                Long tRefId = parseRefId(trainingRefId);
                Long bpRefId = parseRefId(bodyPartRefId);

                // findAll() loads everything into memory first so lazy collections
                // (targetMuscles, equipments) can be fetched without an open cursor conflict
                exerciseRepository.findAll().stream()
                        .filter(e -> tRefId == null || (e.getTraining() != null && e.getTraining().getRefId() == tRefId))
                        .filter(e -> bpRefId == null || (e.getTargetBodyPart() != null && e.getTargetBodyPart().getRefId() == bpRefId))
                        .forEach(exercise -> {
                            String muscles = exercise.getTargetMuscles() == null ? "" :
                                    exercise.getTargetMuscles().stream()
                                            .map(Muscle::getName)
                                            .collect(Collectors.joining("|"));
                            String equipments = exercise.getEquipments() == null ? "" :
                                    String.join("|", exercise.getEquipments());

                            writer.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                                    csv(String.valueOf(exercise.getRefId())),
                                    csv(exercise.getName()),
                                    csv(exercise.getDescription()),
                                    csv(exercise.getUnit()),
                                    csv(Status.ApplicationStatus.getStatusStr(exercise.getStatus())),
                                    csv(exercise.getTraining() != null ? exercise.getTraining().getName() : ""),
                                    csv(exercise.getTargetBodyPart() != null ? exercise.getTargetBodyPart().getName() : ""),
                                    csv(muscles),
                                    csv(equipments),
                                    csv(DifficultyLevel.parse(exercise.getDifficultyLevel()).getCategory()),
                                    csv(RiskLevel.parse(exercise.getRiskLevel()).getCategory()));
                            writer.flush();
                        });
            } catch (Exception e) {
                log.error("Error during exercise export", e);
            }
            return null;
        });
    }

    @Override
    public StreamingResponseBody exportRoutines(String username, LocalDate fromDate, LocalDate toDate, String status) {
        return outputStream -> transactionTemplate.execute(txStatus -> {
            try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream))) {
                writer.println("Username,RefID,WorkoutDate,Status,TrainingType,BurntCalories,DurationMinutes");

                Integer statusFilter = parseRoutineStatus(status);
                routineRepository.findByUsernameAndRoutineDateBetween(username, fromDate, toDate).stream()
                        .filter(r -> statusFilter == null || r.getStatus() == statusFilter)
                        .forEach(routine -> {
                            writer.printf("%s,%s,%s,%s,%s,%.2f,%.2f%n",
                                    csv(routine.getUsername()),
                                    csv(String.valueOf(routine.getRefId())),
                                    csv(routine.getRoutineDate() == null ? "" : routine.getRoutineDate().toString()),
                                    csv(Status.RoutineStatus.toString(routine.getStatus())),
                                    csv(routine.getTraining() != null ? routine.getTraining().getName() : ""),
                                    routine.getBurntCalories(),
                                    routine.getDurationInMinutes());
                            writer.flush();
                        });
            } catch (Exception e) {
                log.error("Error during routine export", e);
            }
            return null;
        });
    }

    @Override
    public StreamingResponseBody exportRoutineDrills(String username, LocalDate fromDate, LocalDate toDate) {
        return outputStream -> transactionTemplate.execute(txStatus -> {
            try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream))) {
                writer.println("Username,RoutineRefID,WorkoutDate,RoutineStatus,TrainingName," +
                        "RoutineBurntCalories,RoutineDurationInMinutes,DrillRefID,ExerciseName," +
                        "ExerciseUnit,ExerciseDifficultyLevel,ExerciseRiskLevel,TargetBodyPart,TargetMuscles," +
                        "DrillMeasurementUnit,DrillMeasurement,DrillUnit,DrillRepetition,DrillBurntCalories,DrillNotes");

                List<Routine> routines = routineRepository.findByUsernameAndRoutineDateBetween(username, fromDate, toDate);
                routines.forEach(routine -> {
                    List<Drill> drills = routine.getDrills() == null ? Collections.emptyList() : routine.getDrills();
                    drills.forEach(drill -> {
                        RoutineDrillExportDTO row = buildDrillRow(routine, drill);
                        writer.printf("%s,%s,%s,%s,%s,%.2f,%.2f,%s,%s,%s,%s,%s,%s,%s,%s,%.2f,%s,%d,%.2f,%s%n",
                                csv(row.getUsername()),
                                csv(row.getRoutineRefId()),
                                csv(row.getWorkoutDate() == null ? "" : row.getWorkoutDate().toString()),
                                csv(row.getRoutineStatus()),
                                csv(row.getTrainingName()),
                                row.getRoutineBurntCalories(),
                                row.getRoutineDurationInMinutes(),
                                csv(row.getDrillRefId()),
                                csv(row.getExerciseName()),
                                csv(row.getExerciseUnit()),
                                csv(row.getExerciseDifficultyLevel()),
                                csv(row.getExerciseRiskLevel()),
                                csv(row.getTargetBodyPart()),
                                csv(row.getTargetMuscles()),
                                csv(row.getDrillMeasurementUnit()),
                                row.getDrillMeasurement(),
                                csv(row.getDrillUnit()),
                                row.getDrillRepetition(),
                                row.getDrillBurntCalories(),
                                csv(row.getDrillNotes()));
                    });
                    writer.flush();
                });
            } catch (Exception e) {
                log.error("Error during routine drill export", e);
            }
            return null;
        });
    }

    @Override
    public StreamingResponseBody exportNutrition(String username, LocalDate fromDate, LocalDate toDate, String mealType) {
        return outputStream -> transactionTemplate.execute(txStatus -> {
            try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream))) {
                writer.println("Username,RefID,EntryDate,EntryTime,MealType,FoodName,ServingQty,ServingUnit," +
                        "Calories,Protein,Carbs,Fat,Fiber,Sugar,Sodium,SourceType,Notes");

                foodEntryRepository
                        .findByUsernameAndEntryDateBetweenOrderByEntryDateAscEntryTimeAsc(username, fromDate, toDate)
                        .stream()
                        .filter(e -> StringUtils.isBlank(mealType) || StringUtils.equalsIgnoreCase(mealType, e.getMealType()))
                        .forEach(entry -> {
                            writer.printf("%s,%s,%s,%s,%s,%s,%.2f,%s,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%s,%s%n",
                                    csv(entry.getUsername()),
                                    csv(String.valueOf(entry.getRefId())),
                                    csv(entry.getEntryDate() == null ? "" : entry.getEntryDate().toString()),
                                    csv(entry.getEntryTime() == null ? "" : entry.getEntryTime().toString()),
                                    csv(entry.getMealType()),
                                    csv(entry.getFoodName()),
                                    entry.getServingQty(),
                                    csv(entry.getServingUnit()),
                                    entry.getCalories(),
                                    entry.getProtein(),
                                    entry.getCarbs(),
                                    entry.getFat(),
                                    entry.getFiber(),
                                    entry.getSugar(),
                                    entry.getSodium(),
                                    csv(entry.getSourceType()),
                                    csv(entry.getNotes()));
                            writer.flush();
                        });
            } catch (Exception e) {
                log.error("Error during nutrition export", e);
            }
            return null;
        });
    }

    private RoutineDrillExportDTO buildDrillRow(Routine routine, Drill drill) {
        Exercise exercise = drill.getExercise();
        String muscles = "";
        String bodyPart = "";
        String difficulty = "";
        String risk = "";
        String exerciseUnit = "";

        if (exercise != null) {
            // targetMuscles is LAZY — accessed here within the active transactionTemplate session
            muscles = exercise.getTargetMuscles() == null ? "" :
                    exercise.getTargetMuscles().stream()
                            .map(Muscle::getName)
                            .collect(Collectors.joining("|"));
            bodyPart = exercise.getTargetBodyPart() != null ? exercise.getTargetBodyPart().getName() : "";
            difficulty = DifficultyLevel.parse(exercise.getDifficultyLevel()).getCategory();
            risk = RiskLevel.parse(exercise.getRiskLevel()).getCategory();
            exerciseUnit = exercise.getUnit() != null ? exercise.getUnit() : "";
        }

        return RoutineDrillExportDTO.builder()
                .username(routine.getUsername())
                .routineRefId(String.valueOf(routine.getRefId()))
                .workoutDate(routine.getRoutineDate())
                .routineStatus(Status.RoutineStatus.toString(routine.getStatus()))
                .trainingName(routine.getTraining() != null ? routine.getTraining().getName() : "")
                .routineBurntCalories(routine.getBurntCalories())
                .routineDurationInMinutes(routine.getDurationInMinutes())
                .drillRefId(String.valueOf(drill.getRefId()))
                .exerciseName(exercise != null ? exercise.getName() : "")
                .exerciseUnit(exerciseUnit)
                .exerciseDifficultyLevel(difficulty)
                .exerciseRiskLevel(risk)
                .targetBodyPart(bodyPart)
                .targetMuscles(muscles)
                .drillMeasurementUnit(drill.getMeasurementUnit())
                .drillMeasurement(drill.getMeasurement())
                .drillUnit(drill.getUnit())
                .drillRepetition(drill.getRepetition())
                .drillBurntCalories(drill.getBurntCalories())
                .drillNotes(drill.getNotes())
                .build();
    }

    private String csv(String value) {
        String safe = value == null ? "" : value;
        return "\"" + safe.replace("\"", "\"\"") + "\"";
    }

    private Long parseRefId(String refId) {
        if (StringUtils.isBlank(refId)) return null;
        try {
            return Long.parseLong(refId.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer parseRoutineStatus(String status) {
        if (StringUtils.isBlank(status)) return null;
        try {
            return Status.RoutineStatus.toInt(status.trim().toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }
}
