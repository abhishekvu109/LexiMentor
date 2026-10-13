package com.abhi.leximentor.fitmate.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

@Builder
@ToString
@EqualsAndHashCode
@Data
public class MuscleBodyPartFocusDTO {
    private Map<String, Long> bodyPartCounts;
    private Map<String, Double> bodyPartVolume;
    private Map<String, Long> muscleCounts;
}
