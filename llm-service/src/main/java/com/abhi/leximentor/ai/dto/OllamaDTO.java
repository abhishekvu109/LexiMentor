package com.abhi.leximentor.ai.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

@Data
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class OllamaDTO {
    private String model;
    private boolean stream;
    private JsonNode format;
    private String prompt;
}
