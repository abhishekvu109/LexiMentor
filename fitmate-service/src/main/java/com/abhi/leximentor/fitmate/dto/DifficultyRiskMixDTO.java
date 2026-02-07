package com.abhi.leximentor.fitmate.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

@Builder
@ToString
@EqualsAndHashCode
@Data
public class DifficultyRiskMixDTO {
    private double averageDifficultyLevel;
    private double averageRiskLevel;
    private Map<Integer, Long> difficultyCounts;
    private Map<Integer, Long> riskCounts;
}
