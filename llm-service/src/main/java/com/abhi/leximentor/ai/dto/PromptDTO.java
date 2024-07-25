package com.abhi.leximentor.ai.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@Builder
@Data
public class PromptDTO {
    private String prompt;
}
