package com.abhi.leximentor.fitmate.service.impl;

import com.abhi.leximentor.fitmate.constants.Status;
import com.abhi.leximentor.fitmate.dto.FoodEntryDTO;
import com.abhi.leximentor.fitmate.dto.ExerciseDTO;
import com.abhi.leximentor.fitmate.dto.RoutineDrillExportDTO;
import com.abhi.leximentor.fitmate.dto.RoutineDTO;
import com.abhi.leximentor.fitmate.entities.FoodEntry;
import com.abhi.leximentor.fitmate.entities.Drill;
import com.abhi.leximentor.fitmate.entities.Exercise;
import com.abhi.leximentor.fitmate.entities.Routine;
import com.abhi.leximentor.fitmate.mapper.ExerciseMapper;
import com.abhi.leximentor.fitmate.mapper.RoutineMapper;
import com.abhi.leximentor.fitmate.repository.FoodEntryRepository;
import com.abhi.leximentor.fitmate.repository.ExerciseRepository;
import com.abhi.leximentor.fitmate.repository.RoutineRepository;
import com.abhi.leximentor.fitmate.service.ExportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SequenceWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {

    private final ExerciseRepository exerciseRepository;
    private final RoutineRepository routineRepository;
    private final FoodEntryRepository foodEntryRepository;
    private final ObjectMapper objectMapper;
    private final TransactionTemplate transactionTemplate;
    private final ExerciseMapper exerciseMapper;
    private final RoutineMapper routineMapper;

    @Override
    @Transactional
    public StreamingResponseBody exportExercises(String format) {
        return outputStream -> transactionTemplate.execute(status -> {
            try (Stream<Exercise> exerciseStream = exerciseRepository.findAllBy()) {

                if ("csv".equalsIgnoreCase(format)) {
                    try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream))) {
                        writer.println("RefID,Name,Description,Unit,Status,BodyPart");
                        exerciseStream.forEach(exercise -> {
                            ExerciseDTO dto = exerciseMapper.toDto(exercise);
                            writer.printf("%s,\"%s\",\"%s\",%s,%s,%s%n",
                                    dto.getRefId(),
                                    dto.getName(),
                                    dto.getDescription() != null ? dto.getDescription().replace("\"", "\"\"") : "",
                                    dto.getUnit(),
                                    dto.getStatus(),
                                    dto.getBodyPart() != null ? dto.getBodyPart().getName() : "");
                            writer.flush();
                        });
                    }
                } else {
                    try (SequenceWriter sequenceWriter = objectMapper.writer().writeValues(outputStream)) {
                        sequenceWriter.init(true);
                        exerciseStream.forEach(exercise -> {
                            try {
                                sequenceWriter.write(exerciseMapper.toDto(exercise));
                            } catch (Exception e) {
                                log.error("Error writing exercise to JSON stream", e);
                            }
                        });
                    }
                }
            } catch (Exception e) {
                log.error("Error during exercise export", e);
            }
            return null;
        });
    }

    @Override
    @Transactional
    public StreamingResponseBody exportRoutines(String format) {
        return outputStream -> transactionTemplate.execute(status -> {
            try (Stream<Routine> routineStream = routineRepository.findAllBy()) {

                if ("csv".equalsIgnoreCase(format)) {
                    try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream))) {
                        writer.println("RefID,WorkoutDate,Description,Status,BurntCalories,DurationMinutes");
                        routineStream.forEach(routine -> {
                            RoutineDTO dto = routineMapper.toDto(routine);
                            writer.printf("%s,%s,\"%s\",%s,%.2f,%.2f%n",
                                    dto.getRefId(),
                                    dto.getWorkoutDate(),
                                    dto.getDescription() != null ? dto.getDescription().replace("\"", "\"\"") : "",
                                    dto.getStatus(),
                                    dto.getBurntCalories(),
                                    dto.getDurationInMinutes());
                            writer.flush();
                        });
                    }
                } else {
                    try (SequenceWriter sequenceWriter = objectMapper.writer().writeValues(outputStream)) {
                        sequenceWriter.init(true);
                        routineStream.forEach(routine -> {
                            try {
                                sequenceWriter.write(routineMapper.toDto(routine));
                            } catch (Exception e) {
                                log.error("Error writing routine to JSON stream", e);
                            }
                        });
                    }
                }
            } catch (Exception e) {
                log.error("Error during routine export", e);
            }
            return null;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public StreamingResponseBody exportRoutineDrills(String format, String username, LocalDate fromDate, LocalDate toDate) {
        return outputStream -> transactionTemplate.execute(status -> {
            try {
                List<Routine> routines = routineRepository.findByUsernameAndRoutineDateBetween(username, fromDate, toDate);
                List<RoutineDrillExportDTO> rows = routines.stream()
                        .flatMap(routine -> {
                            List<Drill> drills = routine.getDrills() == null ? Collections.emptyList() : routine.getDrills();
                            return drills.stream().map(drill -> buildRoutineDrillExportRow(routine, drill));
                        })
                        .toList();

                if ("csv".equalsIgnoreCase(format)) {
                    try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream))) {
                        writer.println("Username,RoutineRefID,WorkoutDate,RoutineStatus,TrainingName,RoutineBurntCalories,RoutineDurationInMinutes,DrillRefID,ExerciseName,DrillMeasurementUnit,DrillMeasurement,DrillUnit,DrillRepetition,DrillBurntCalories,DrillNotes");
                        rows.forEach(row -> writer.printf("%s,%s,%s,%s,%s,%.2f,%.2f,%s,%s,%s,%.2f,%s,%d,%.2f,%s%n",
                                csv(row.getUsername()),
                                csv(row.getRoutineRefId()),
                                csv(row.getWorkoutDate() == null ? "" : row.getWorkoutDate().toString()),
                                csv(row.getRoutineStatus()),
                                csv(row.getTrainingName()),
                                row.getRoutineBurntCalories(),
                                row.getRoutineDurationInMinutes(),
                                csv(row.getDrillRefId()),
                                csv(row.getExerciseName()),
                                csv(row.getDrillMeasurementUnit()),
                                row.getDrillMeasurement(),
                                csv(row.getDrillUnit()),
                                row.getDrillRepetition(),
                                row.getDrillBurntCalories(),
                                csv(row.getDrillNotes())));
                        writer.flush();
                    }
                } else {
                    try (SequenceWriter sequenceWriter = objectMapper.writer().writeValues(outputStream)) {
                        sequenceWriter.init(true);
                        rows.forEach(row -> {
                            try {
                                sequenceWriter.write(row);
                            } catch (Exception e) {
                                log.error("Error writing routine drill to JSON stream", e);
                            }
                        });
                    }
                }
            } catch (Exception e) {
                log.error("Error during routine drill export", e);
            }
            return null;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public StreamingResponseBody exportNutrition(String format, String username, LocalDate fromDate, LocalDate toDate) {
        return outputStream -> transactionTemplate.execute(status -> {
            try {
                List<FoodEntryDTO> rows = foodEntryRepository
                        .findByUsernameAndEntryDateBetweenOrderByEntryDateAscEntryTimeAsc(username, fromDate, toDate)
                        .stream()
                        .map(this::toFoodEntryDTO)
                        .toList();

                if ("csv".equalsIgnoreCase(format)) {
                    try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream))) {
                        writer.println("Username,RefID,EntryDate,EntryTime,MealType,FoodName,ServingQty,ServingUnit,Calories,Protein,Carbs,Fat,Fiber,Sugar,Sodium,SourceType,Notes");
                        rows.forEach(row -> writer.printf("%s,%s,%s,%s,%s,%s,%.2f,%s,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%s,%s%n",
                                csv(row.getUsername()),
                                csv(row.getRefId()),
                                csv(row.getEntryDate() == null ? "" : row.getEntryDate().toString()),
                                csv(row.getEntryTime() == null ? "" : row.getEntryTime().toString()),
                                csv(row.getMealType()),
                                csv(row.getFoodName()),
                                row.getServingQty(),
                                csv(row.getServingUnit()),
                                row.getCalories(),
                                row.getProtein(),
                                row.getCarbs(),
                                row.getFat(),
                                row.getFiber(),
                                row.getSugar(),
                                row.getSodium(),
                                csv(row.getSourceType()),
                                csv(row.getNotes())));
                        writer.flush();
                    }
                } else {
                    try (SequenceWriter sequenceWriter = objectMapper.writer().writeValues(outputStream)) {
                        sequenceWriter.init(true);
                        rows.forEach(row -> {
                            try {
                                sequenceWriter.write(row);
                            } catch (Exception e) {
                                log.error("Error writing nutrition row to JSON stream", e);
                            }
                        });
                    }
                }
            } catch (Exception e) {
                log.error("Error during nutrition export", e);
            }
            return null;
        });
    }

    private RoutineDrillExportDTO buildRoutineDrillExportRow(Routine routine, Drill drill) {
        return RoutineDrillExportDTO.builder()
                .username(routine.getUsername())
                .routineRefId(String.valueOf(routine.getRefId()))
                .workoutDate(routine.getRoutineDate())
                .routineStatus(Status.RoutineStatus.toString(routine.getStatus()))
                .trainingName(routine.getTraining() != null ? routine.getTraining().getName() : "")
                .routineBurntCalories(routine.getBurntCalories())
                .routineDurationInMinutes(routine.getDurationInMinutes())
                .drillRefId(String.valueOf(drill.getRefId()))
                .exerciseName(drill.getExercise() != null ? drill.getExercise().getName() : "")
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

    private FoodEntryDTO toFoodEntryDTO(FoodEntry entry) {
        return FoodEntryDTO.builder()
                .refId(String.valueOf(entry.getRefId()))
                .username(entry.getUsername())
                .entryDate(entry.getEntryDate())
                .entryTime(entry.getEntryTime())
                .mealType(entry.getMealType())
                .foodName(entry.getFoodName())
                .servingQty(entry.getServingQty())
                .servingUnit(entry.getServingUnit())
                .calories(entry.getCalories())
                .protein(entry.getProtein())
                .carbs(entry.getCarbs())
                .fat(entry.getFat())
                .fiber(entry.getFiber())
                .sugar(entry.getSugar())
                .sodium(entry.getSodium())
                .sourceType(entry.getSourceType())
                .notes(entry.getNotes())
                .crtnDate(entry.getCrtnDate())
                .lastUpdDate(entry.getLastUpdDate())
                .build();
    }

}
