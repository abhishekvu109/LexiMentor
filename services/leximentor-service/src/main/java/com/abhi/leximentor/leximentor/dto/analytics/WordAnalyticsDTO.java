package com.abhi.leximentor.leximentor.dto.analytics;

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
public class WordAnalyticsDTO {
    private long totalWords;
    private Map<String, Integer> posDistribution;
}
