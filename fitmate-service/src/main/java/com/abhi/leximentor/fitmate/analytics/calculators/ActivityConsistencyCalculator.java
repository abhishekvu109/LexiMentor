package com.abhi.leximentor.fitmate.analytics.calculators;

import com.abhi.leximentor.fitmate.analytics.AnalyticsCalculator;
import com.abhi.leximentor.fitmate.analytics.AnalyticsContext;
import com.abhi.leximentor.fitmate.dto.ActivityConsistencyDTO;
import com.abhi.leximentor.fitmate.dto.AnalyticsDTO;
import com.abhi.leximentor.fitmate.dto.TimeBucketCountDTO;
import com.abhi.leximentor.fitmate.entities.Routine;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ActivityConsistencyCalculator implements AnalyticsCalculator {
    @Override
    public void apply(AnalyticsContext context, AnalyticsDTO.AnalyticsDTOBuilder builder) {
        List<LocalDate> routineDates = context.getRoutines().stream()
                .map(Routine::getRoutineDate)
                .filter(date -> date != null)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        long totalActiveDays = routineDates.size();

        int longestStreak = 0;
        int currentStreak = 0;
        LocalDate prevDate = null;
        for (LocalDate date : routineDates) {
            if (prevDate == null || date.equals(prevDate.plusDays(1))) {
                currentStreak++;
            } else {
                currentStreak = 1;
            }
            longestStreak = Math.max(longestStreak, currentStreak);
            prevDate = date;
        }

        int currentStreakDays = 0;
        LocalDate lastActiveDate = routineDates.isEmpty() ? null : routineDates.get(routineDates.size() - 1);
        if (lastActiveDate != null) {
            currentStreakDays = 1;
            for (int i = routineDates.size() - 2; i >= 0; i--) {
                LocalDate date = routineDates.get(i);
                LocalDate next = routineDates.get(i + 1);
                if (next.equals(date.plusDays(1))) {
                    currentStreakDays++;
                } else {
                    break;
                }
            }
        }

        List<Integer> restGaps = new ArrayList<>();
        for (int i = 1; i < routineDates.size(); i++) {
            long diff = ChronoUnit.DAYS.between(routineDates.get(i - 1), routineDates.get(i));
            if (diff > 1) {
                restGaps.add((int) (diff - 1));
            }
        }
        double averageRestDays = restGaps.isEmpty() ? 0.0 : restGaps.stream().mapToInt(Integer::intValue).average().orElse(0.0);
        int maxRestDays = restGaps.stream().mapToInt(Integer::intValue).max().orElse(0);

        Map<LocalDate, Long> weeklyCounts = context.getRoutines().stream()
                .filter(routine -> routine.getRoutineDate() != null)
                .collect(Collectors.groupingBy(routine -> weekStart(routine.getRoutineDate()), Collectors.counting()));

        List<TimeBucketCountDTO> workoutsPerWeek = weeklyCounts.entrySet().stream()
                .map(entry -> TimeBucketCountDTO.builder()
                        .date(entry.getKey())
                        .count(entry.getValue())
                        .build())
                .sorted(Comparator.comparing(TimeBucketCountDTO::getDate))
                .collect(Collectors.toList());

        Map<LocalDate, Long> monthlyCounts = context.getRoutines().stream()
                .filter(routine -> routine.getRoutineDate() != null)
                .collect(Collectors.groupingBy(routine -> monthStart(routine.getRoutineDate()), Collectors.counting()));

        List<TimeBucketCountDTO> workoutsPerMonth = monthlyCounts.entrySet().stream()
                .map(entry -> TimeBucketCountDTO.builder()
                        .date(entry.getKey())
                        .count(entry.getValue())
                        .build())
                .sorted(Comparator.comparing(TimeBucketCountDTO::getDate))
                .collect(Collectors.toList());

        builder.activityConsistency(ActivityConsistencyDTO.builder()
                .totalActiveDays(totalActiveDays)
                .longestStreakDays(longestStreak)
                .currentStreakDays(currentStreakDays)
                .averageRestDays(averageRestDays)
                .maxRestDays(maxRestDays)
                .lastActiveDate(lastActiveDate)
                .workoutsPerWeek(workoutsPerWeek)
                .workoutsPerMonth(workoutsPerMonth)
                .build());
    }

    private LocalDate weekStart(LocalDate date) {
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    private LocalDate monthStart(LocalDate date) {
        return date.withDayOfMonth(1);
    }
}
