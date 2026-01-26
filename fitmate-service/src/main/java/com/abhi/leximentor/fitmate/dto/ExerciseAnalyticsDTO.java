package com.abhi.leximentor.fitmate.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Builder
@ToString
@EqualsAndHashCode
@Data
public class ExerciseAnalyticsDTO {
    private int totalNumberOfTimesCompleted;
    private double monthlyAverage;
    private double weeklyAverage;
}
