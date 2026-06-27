package com.abhi.leximentor.fitmate.analytics;

import com.abhi.leximentor.fitmate.entities.Drill;
import com.abhi.leximentor.fitmate.entities.Exercise;
import com.abhi.leximentor.fitmate.entities.Routine;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class AnalyticsContext {
    private final String username;
    private final LocalDate today;
    private final List<Routine> routines;
    private final List<Drill> drills;
    private final List<Exercise> allExercises;
    private final Map<String, List<Drill>> drillsByExerciseName;
    private final Map<LocalDate, List<Routine>> routinesByDate;

    private AnalyticsContext(String username,
                             LocalDate today,
                             List<Routine> routines,
                             List<Drill> drills,
                             List<Exercise> allExercises,
                             Map<String, List<Drill>> drillsByExerciseName,
                             Map<LocalDate, List<Routine>> routinesByDate) {
        this.username = username;
        this.today = today;
        this.routines = routines;
        this.drills = drills;
        this.allExercises = allExercises;
        this.drillsByExerciseName = drillsByExerciseName;
        this.routinesByDate = routinesByDate;
    }

    public static AnalyticsContext from(String username, List<Routine> routines, List<Exercise> allExercises) {
        LocalDate today = LocalDate.now();
        List<Routine> safeRoutines = routines == null ? Collections.emptyList() : routines.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<Drill> drills = new ArrayList<>();
        for (Routine routine : safeRoutines) {
            if (routine.getDrills() == null) {
                continue;
            }
            for (Drill drill : routine.getDrills()) {
                if (drill != null) {
                    drills.add(drill);
                }
            }
        }

        Map<String, List<Drill>> drillsByExerciseName = drills.stream()
                .filter(drill -> drill.getExercise() != null && drill.getExercise().getName() != null)
                .collect(Collectors.groupingBy(drill -> drill.getExercise().getName()));

        Map<LocalDate, List<Routine>> routinesByDate = safeRoutines.stream()
                .filter(routine -> routine.getRoutineDate() != null)
                .collect(Collectors.groupingBy(Routine::getRoutineDate));

        List<Exercise> safeExercises = allExercises == null ? Collections.emptyList() : allExercises.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new AnalyticsContext(username, today, safeRoutines, drills, safeExercises, drillsByExerciseName, routinesByDate);
    }

    public String getUsername() {
        return username;
    }

    public LocalDate getToday() {
        return today;
    }

    public List<Routine> getRoutines() {
        return routines;
    }

    public List<Drill> getDrills() {
        return drills;
    }

    public List<Exercise> getAllExercises() {
        return allExercises;
    }

    public Map<String, List<Drill>> getDrillsByExerciseName() {
        return drillsByExerciseName;
    }

    public Map<LocalDate, List<Routine>> getRoutinesByDate() {
        return routinesByDate;
    }
}
