package com.abhi.leximentor.inventory.model;

import com.abhi.leximentor.inventory.entities.drill.DrillMetadata;
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
    private DrillMetadata drillMetadata;
    // Count of challenges group by challenge name
    private Map<String, Long> challengeGroup;
    // Average challenge score
    private Map<String, Double> avgChallengeScore;
    private Map<String, Double> wordDifficultScore;


    private List<DrillMetadata> drillMetadataList;
    private Map<String, Double> topDifficultWordByAvgScore;

}
