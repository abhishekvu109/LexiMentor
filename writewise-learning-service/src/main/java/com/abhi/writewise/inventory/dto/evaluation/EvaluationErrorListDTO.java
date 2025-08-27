package com.abhi.writewise.inventory.dto.evaluation;

import lombok.*;

@Data
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class EvaluationErrorListDTO {
    private long refId;
    private String uuid;
    private String id;
    private int start;
    private int end;
    private String type;
    private String subType;
    private String incorrectText;
    private String correctText;
    private String explanation;
}
