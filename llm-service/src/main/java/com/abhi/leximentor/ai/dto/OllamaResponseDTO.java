package com.abhi.leximentor.ai.dto;

import lombok.*;

@Data
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class OllamaResponseDTO {
    private String response;
    private String model;

}
