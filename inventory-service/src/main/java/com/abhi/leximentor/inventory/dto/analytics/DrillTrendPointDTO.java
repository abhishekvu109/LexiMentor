package com.abhi.leximentor.inventory.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;

@Builder
@EqualsAndHashCode
@Data
@ToString
@AllArgsConstructor
public class DrillTrendPointDTO {
    private LocalDate date;
    private double avgScore;
    private long drillCount;
}
