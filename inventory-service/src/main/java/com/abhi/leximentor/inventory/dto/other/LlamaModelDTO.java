package com.abhi.leximentor.inventory.dto.other;

import lombok.*;

@Data
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class LlamaModelDTO {
    private String text;
    private int confidence;
    private String explanation;
    private boolean isCorrect;
    private String error;
    private String modelResponse;

    public static LlamaModelDTO getDefaultInstance() {
        return LlamaModelDTO.builder().confidence(0).isCorrect(false).explanation("EVALUATION_FAILED").build();
    }
}
