package com.abhi.leximentor.inventory.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

@Builder
@EqualsAndHashCode
@Data
@ToString
@AllArgsConstructor
public class WordDistributionDTO {
    private long totalWords;
    private long unusedWords;
    private double unusedPercentage;
    private Map<String, Long> sourceDistribution;
    private Map<String, Long> categoryDistribution;
}
