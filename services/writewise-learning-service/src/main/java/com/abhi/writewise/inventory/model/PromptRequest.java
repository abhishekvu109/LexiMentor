package com.abhi.writewise.inventory.model;

import lombok.*;

@Builder
@Data
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class PromptRequest {
    private String format;
    private String model;
    private String prompt;
    private OllamaOptionsDTO options;
}
