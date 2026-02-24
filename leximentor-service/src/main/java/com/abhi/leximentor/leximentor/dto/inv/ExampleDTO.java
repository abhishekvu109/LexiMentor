package com.abhi.leximentor.leximentor.dto.inv;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@ToString
public class ExampleDTO {
    private String key;
    private String wordKey;
    private String word;
    private String example;
    private String source;

}
