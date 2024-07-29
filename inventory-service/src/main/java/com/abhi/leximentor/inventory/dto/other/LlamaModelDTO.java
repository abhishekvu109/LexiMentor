package com.abhi.leximentor.inventory.dto.other;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class LlamaModelDTO {
    private String text;
    @JsonProperty("confidence")
    private int confidence;
    @JsonProperty("explanation")
    private String explanation;
    @JsonProperty("isCorrect")
    private boolean isCorrect;
    private String error;
    private String modelResponse;

    public static LlamaModelDTO getDefaultInstance() {
        return LlamaModelDTO.builder().confidence(0).isCorrect(false).explanation("EVALUATION_FAILED").build();
    }
}
