package com.abhi.leximentor.inventory.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@ToString
public class PartsOfSpeechDTO {
    private String pos;
    private String refId;
    private String wordRefId;
    private String word;
    private String source;
}
