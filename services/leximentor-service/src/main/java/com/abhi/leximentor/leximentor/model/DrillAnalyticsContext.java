package com.abhi.leximentor.leximentor.model;

import com.abhi.leximentor.leximentor.entities.drill.Drill;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Builder
@Data
@EqualsAndHashCode
@ToString
public class DrillAnalyticsContext {
    private Drill drill;
    // Count of challenges group by challenge name
    private Map<String, Long> challengeGroup;
    // Average challenge score
    private Map<String, Double> avgChallengeScore;
    private Map<String, Double> wordDifficultScore;


    private List<Drill> drillList;
    private Map<String, Double> topDifficultWordByAvgScore;

}
