package com.abhi.leximentor.fitmate.analytics.calculators;

import com.abhi.leximentor.fitmate.analytics.AnalyticsCalculator;
import com.abhi.leximentor.fitmate.analytics.AnalyticsContext;
import com.abhi.leximentor.fitmate.analytics.AnalyticsMath;
import com.abhi.leximentor.fitmate.dto.AnalyticsDTO;
import com.abhi.leximentor.fitmate.dto.ExerciseRecordDTO;
import com.abhi.leximentor.fitmate.dto.PersonalRecordsDTO;
import com.abhi.leximentor.fitmate.entities.Drill;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PersonalRecordsCalculator implements AnalyticsCalculator {
    @Override
    public void apply(AnalyticsContext context, AnalyticsDTO.AnalyticsDTOBuilder builder) {
        List<ExerciseRecordDTO> records = context.getDrillsByExerciseName().entrySet().stream()
                .map(entry -> buildRecord(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(ExerciseRecordDTO::getExerciseName, Comparator.nullsLast(String::compareToIgnoreCase)))
                .collect(Collectors.toList());

        builder.personalRecords(PersonalRecordsDTO.builder()
                .exerciseRecords(records)
                .build());
    }

    private ExerciseRecordDTO buildRecord(String exerciseName, List<Drill> drills) {
        Drill maxMeasurementDrill = drills.stream()
                .max(Comparator.comparingDouble(Drill::getMeasurement))
                .orElse(null);
        Drill maxRepsDrill = drills.stream()
                .max(Comparator.comparingInt(Drill::getRepetition))
                .orElse(null);
        Drill maxVolumeDrill = drills.stream()
                .max(Comparator.comparingDouble(AnalyticsMath::volume))
                .orElse(null);

        return ExerciseRecordDTO.builder()
                .exerciseName(exerciseName)
                .maxMeasurement(maxMeasurementDrill == null ? 0.0 : maxMeasurementDrill.getMeasurement())
                .maxMeasurementDate(toDate(maxMeasurementDrill))
                .maxRepetitions(maxRepsDrill == null ? 0 : maxRepsDrill.getRepetition())
                .maxRepetitionsDate(toDate(maxRepsDrill))
                .maxVolume(maxVolumeDrill == null ? 0.0 : AnalyticsMath.volume(maxVolumeDrill))
                .maxVolumeDate(toDate(maxVolumeDrill))
                .build();
    }

    private LocalDate toDate(Drill drill) {
        return drill == null || drill.getCrtnDate() == null ? null : drill.getCrtnDate().toLocalDate();
    }
}
