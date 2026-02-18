package com.abhi.leximentor.leximentor.service.analytics.engine.context;

import com.abhi.leximentor.leximentor.dto.analytics.DrillAnalyticsDTO;
import com.abhi.leximentor.leximentor.entities.drill.DrillMetadata;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DrillAnalyticsContext {
    private long drillRefId;
    private int topN;
    private DrillMetadata drillMetadata;
    private DrillAnalyticsDTO.DrillAnalyticsDTOBuilder builder;
}
