package com.abhi.leximentor.leximentor.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Builder
@EqualsAndHashCode
@Data
@ToString
@AllArgsConstructor
public class UserPerformanceDTO {
    private String username;
    private List<DrillTypeUserPerformanceDTO> topBest;
    private List<DrillTypeUserPerformanceDTO> topWorst;
}
