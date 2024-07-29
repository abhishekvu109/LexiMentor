package com.abhi.leximentor.ai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@EqualsAndHashCode
@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class MeaningEvaluationDTO {
    private String prompt;
    @JsonProperty("confidence")
    private int confidence;
    @JsonProperty("explanation")
    private String explanation;
    @JsonProperty("isCorrect")
    private boolean isCorrect;
    private String error;
    private String modelResponse;

    public static MeaningEvaluationDTO getDefaultInstance() {
        return MeaningEvaluationDTO.builder().confidence(0).isCorrect(false).explanation("EVALUATION_FAILED").build();
    }
}
