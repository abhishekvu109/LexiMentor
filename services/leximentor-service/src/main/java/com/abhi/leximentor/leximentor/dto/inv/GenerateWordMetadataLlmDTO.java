package com.abhi.leximentor.leximentor.dto.inv;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;

@Builder
@Data
@ToString
@EqualsAndHashCode
public class GenerateWordMetadataLlmDTO {
    private Collection<String> words;
    private String prompt;
    private String response;
}
