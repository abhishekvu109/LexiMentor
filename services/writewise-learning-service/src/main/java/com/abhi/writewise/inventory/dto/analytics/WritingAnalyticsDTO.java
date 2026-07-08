package com.abhi.writewise.inventory.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WritingAnalyticsDTO {
    private long totalEssays;
    private double overallAverageScore;
    private double improvementRate;
    private List<ScoreTrendDTO> scoreTrend;
    private Map<String, Double> categoryAverages;
    private Map<String, Long> errorDistribution;
    private List<TopErrorDTO> topErrors;
    private List<String> strengths;
    private List<String> weaknesses;
}
