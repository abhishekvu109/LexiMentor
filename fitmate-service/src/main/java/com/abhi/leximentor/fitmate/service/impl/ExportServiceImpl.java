package com.abhi.leximentor.fitmate.service.impl;

import com.abhi.leximentor.fitmate.dto.ExerciseDTO;
import com.abhi.leximentor.fitmate.dto.RoutineDTO;
import com.abhi.leximentor.fitmate.entities.Exercise;
import com.abhi.leximentor.fitmate.entities.Routine;
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
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {

    private final ExerciseRepository exerciseRepository;
    private final RoutineRepository routineRepository;
    private final ObjectMapper objectMapper;
    private final TransactionTemplate transactionTemplate;

    @Override
    @Transactional
    public StreamingResponseBody exportExercises(String format) {
        return outputStream -> transactionTemplate.execute(status -> {
            try (Stream<Exercise> exerciseStream = exerciseRepository.findAllBy()) {

                if ("csv".equalsIgnoreCase(format)) {
                    try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream))) {
                        writer.println("RefID,Name,Description,Unit,Status,BodyPart");
                        exerciseStream.forEach(exercise -> {
                            ExerciseDTO dto = FitmateServiceUtil.ExerciseUtil.buildDto(exercise);
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
                                sequenceWriter.write(FitmateServiceUtil.ExerciseUtil.buildDto(exercise));
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
                            RoutineDTO dto = FitmateServiceUtil.RoutineUtil.buildDto(routine);
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
                                sequenceWriter.write(FitmateServiceUtil.RoutineUtil.buildDto(routine));
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

}
