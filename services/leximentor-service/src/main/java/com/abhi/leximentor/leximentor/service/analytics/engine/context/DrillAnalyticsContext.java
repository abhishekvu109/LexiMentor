package com.abhi.leximentor.leximentor.service.analytics.engine.context;

import com.abhi.leximentor.leximentor.dto.analytics.DrillAnalyticsDTO;
import com.abhi.leximentor.leximentor.entities.drill.Drill;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DrillAnalyticsContext {
    private String drillKey;
    private int topN;
    private Drill drill;
    private DrillAnalyticsDTO.DrillAnalyticsDTOBuilder builder;
}
