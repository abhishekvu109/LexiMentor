package com.abhi.leximentor.fitmate.dto.filters;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Builder
@Data
public class ExerciseSearchFilter {
    private String uuid;
    private String refId;
    private String name;
    private String unit;
    private String status;
    private String trainingRefId;
    private String targetBodyPartRefId;
//    private String difficultyLevel;
//    private String riskLevel;

    public boolean isEmpty() {
        return StringUtils.isAllEmpty(uuid, refId, name, status, trainingRefId, targetBodyPartRefId);
    }
}
