package com.abhi.leximentor.fitmate.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RoutineSearchFilter {
    private String refId;
    private String uuid;
    private String status;
    private String sortBy="crtnDate";
    private String sortDir="desc";
}
