package com.abhi.leximentor.ai.dto;

import lombok.*;

@EqualsAndHashCode
@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeaningEvaluationDTO {
    private String text;
    private int confidence;
    private String explanation;
    private boolean isCorrect;
    private String error;
    private String modelResponse;

    public static MeaningEvaluationDTO getDefaultInstance() {
        return MeaningEvaluationDTO.builder().confidence(0).isCorrect(false).explanation("EVALUATION_FAILED").build();
    }
}
