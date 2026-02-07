package com.abhi.leximentor.fitmate.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Builder
@ToString
@EqualsAndHashCode
@Data
public class ActivityConsistencyDTO {
    private long totalActiveDays;
    private int longestStreakDays;
    private int currentStreakDays;
    private double averageRestDays;
    private int maxRestDays;
    private LocalDate lastActiveDate;
    private List<TimeBucketCountDTO> workoutsPerWeek;
    private List<TimeBucketCountDTO> workoutsPerMonth;
}
