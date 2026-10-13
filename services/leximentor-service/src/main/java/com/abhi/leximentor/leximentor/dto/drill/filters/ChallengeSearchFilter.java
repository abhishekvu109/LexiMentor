package com.abhi.leximentor.leximentor.dto.drill.filters;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
@ToString
@Data
public class ChallengeSearchFilter {
    private String key;
    private String drillKey;
    private String challengeType;
    private String status;
    private String evaluationStatus;
    private String username;
    private Double scoreFrom;
    private Double scoreTo;
    private String sortBy="createdAt";
    private String sortDir="desc";
}
