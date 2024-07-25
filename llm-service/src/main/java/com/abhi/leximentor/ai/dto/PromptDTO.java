package com.abhi.leximentor.ai.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EqualsAndHashCode
@ToString
@Builder
@Data
public class PromptDTO {
    private String prompt;
}
