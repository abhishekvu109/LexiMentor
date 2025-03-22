package com.abhi.writewise.inventory.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WordDefinitionDTO {
    @JsonProperty(value = "words")
    private List<String> words;
    @JsonProperty(value = "prompt")
    private String prompt;
    @JsonProperty(value = "response")
    private String response;
}
