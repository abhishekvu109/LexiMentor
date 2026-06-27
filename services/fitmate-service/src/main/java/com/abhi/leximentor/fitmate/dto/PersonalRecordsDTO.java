package com.abhi.leximentor.fitmate.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Builder
@ToString
@EqualsAndHashCode
@Data
public class PersonalRecordsDTO {
    private List<ExerciseRecordDTO> exerciseRecords;
}
