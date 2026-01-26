package com.abhi.leximentor.fitmate.dto.filters;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

@Data
@ToString
@Builder
public class RoutineSearchFilter {
    private String refId;
    private String uuid;
    private String status;
    private String username;
    private LocalDateTime routineDateFrom;
    private LocalDateTime routineDateTo;
    private String trainingRefId;
    private String sortBy = "routineDate";
    private String sortDir = "desc";

    public static RoutineSearchFilter defaultFilter() {
        return RoutineSearchFilter.builder()
                .sortBy("routineDate")
                .sortDir("desc")
                .build();
    }

    public boolean isEmpty() {
        return StringUtils.isAllEmpty(refId, uuid, status, username, trainingRefId)
                && routineDateFrom == null && routineDateTo == null;
    }
}
