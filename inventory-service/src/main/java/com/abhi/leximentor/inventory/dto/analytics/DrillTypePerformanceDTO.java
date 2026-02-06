package com.abhi.leximentor.inventory.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Builder
@EqualsAndHashCode
@Data
@ToString
@AllArgsConstructor
public class DrillTypePerformanceDTO {
    private String drillType;
    private long drillCount;
    private double avgScore;
    private double passRate;
}
