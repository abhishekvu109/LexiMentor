package com.abhi.leximentor.fitmate.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Builder
@ToString
@EqualsAndHashCode
@Data
public class ExerciseLibraryInsightsDTO {
    private long totalExercises;
    private long usedExercises;
    private long unusedExercises;
    private List<String> unusedExerciseNames;
    private long exercisesWithResources;
    private long exercisesWithoutResources;
}
