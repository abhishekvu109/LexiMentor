package com.abhi.leximentor.inventory.dto.analytics;

import com.abhi.leximentor.inventory.dto.inv.WordDTO;
import lombok.*;

import java.util.List;

@Builder
@EqualsAndHashCode
@Data
@ToString
@AllArgsConstructor
public class DrillAnalyticsDTO {
    private int countOfWordsLearned;
    private double drillSuccessInPercentage;
    private List<WordDTO> topChallengingWordsInTheDrill;
    private double avgDrillScore;
}
