package com.abhi.leximentor.inventory.service.analytics.engine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AnalyticsRequest {
    private AnalyticsType type;
    private Long drillRefId;
    private Integer topN;
    private Integer days;
    private String username;
}
