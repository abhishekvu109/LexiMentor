package com.abhi.writewise.inventory.entities.nosql.mongodb.evaluation;

import com.abhi.writewise.inventory.util.KeyGeneratorUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class EvaluationErrorList {
    @Builder.Default
    private long refId = KeyGeneratorUtil.refId();
    @Builder.Default
    private String uuid = KeyGeneratorUtil.uuid();
    @JsonProperty("id")
    private String id;
    @JsonProperty("start")
    private int start;
    @JsonProperty("end")
    private int end;
    @JsonProperty("type")
    private String type;
    @JsonProperty("subType")
    private String subType;
    @JsonProperty("incorrectText")
    private String incorrectText;
    @JsonProperty("correctedText")
    private String correctedText;
    @JsonProperty("explanation")
    private String explanation;
}
