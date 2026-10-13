package com.abhi.leximentor.leximentor.service.analytics.engine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AnalyticsRequest {
    private AnalyticsType type;
    private String drillKey;
    private Integer topN;
    private Integer days;
    private String username;
}
