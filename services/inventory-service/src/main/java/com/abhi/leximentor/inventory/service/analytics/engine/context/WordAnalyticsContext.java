package com.abhi.leximentor.inventory.service.analytics.engine.context;

import com.abhi.leximentor.inventory.dto.analytics.WordAnalyticsDTO;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class WordAnalyticsContext {
    private WordAnalyticsDTO.WordAnalyticsDTOBuilder builder;
    private Map<String, Integer> posDistribution;
}
