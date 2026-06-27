package com.abhi.leximentor.leximentor.dto.inv;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@ToString
public class PartsOfSpeechDTO {
    private String pos;
    private String key;
    private String wordKey;
    private String word;
    private String source;
}
