package com.abhi.leximentor.inventory.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Builder
@EqualsAndHashCode
@Data
@ToString
@AllArgsConstructor
public class DrillTrendsDTO {
    private LocalDate fromDate;
    private LocalDate toDate;
    private String username;
    private List<DrillTrendPointDTO> points;
}
