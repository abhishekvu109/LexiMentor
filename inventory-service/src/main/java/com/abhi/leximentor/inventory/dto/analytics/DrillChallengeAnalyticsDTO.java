package com.abhi.leximentor.inventory.dto.analytics;

import com.abhi.leximentor.inventory.dto.drill.DrillChallengeDTO;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Builder
@EqualsAndHashCode
@Data
@ToString
@AllArgsConstructor
public class DrillChallengeAnalyticsDTO {
    private String drillType;
    private int drillCount;
    private double avgScore;
    //Should contain score of the challenge that scored the highest
    private double lowestScore;
    // Converse to the previous attribute
    private double highestScore;
    // Should contain the challengeRefIds
    private List<DrillChallengeDTO> topNBestPerformingDrills;
    //Should contain the challengeRefIds
    private List<DrillChallengeDTO> topNWorstPerformingDrills;
}
