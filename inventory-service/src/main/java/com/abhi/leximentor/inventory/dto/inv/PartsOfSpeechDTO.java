package com.abhi.leximentor.inventory.dto.inv;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@ToString
public class PartsOfSpeechDTO {
    private String pos;
    private long refId;
    private long wordRefId;
    private String word;
    private String source;
}
