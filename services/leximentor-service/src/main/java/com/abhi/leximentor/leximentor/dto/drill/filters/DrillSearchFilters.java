package com.abhi.leximentor.leximentor.dto.drill.filters;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
@ToString
@Data
public class DrillSearchFilters {
    private String refId;
    private String uuid;
    private String name;
    private String status;
    private double fromOverAllScore;
    private double toOverAllScore;
}
