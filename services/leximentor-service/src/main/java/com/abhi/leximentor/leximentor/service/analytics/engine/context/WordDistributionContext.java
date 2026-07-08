package com.abhi.leximentor.leximentor.service.analytics.engine.context;

import com.abhi.leximentor.leximentor.dto.analytics.WordDistributionDTO;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class WordDistributionContext {
    private WordDistributionDTO.WordDistributionDTOBuilder builder;
    private long totalWords;
    private long unusedWords;
    private Map<String, Long> sourceDistribution;
    private Map<String, Long> categoryDistribution;
}
