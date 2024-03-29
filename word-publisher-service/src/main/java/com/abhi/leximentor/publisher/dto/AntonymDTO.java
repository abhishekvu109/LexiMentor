package com.abhi.leximentor.publisher.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@ToString
public class AntonymDTO {
    private String antonymKey;
    private String word;
    private String antonym;
    private String source;

}
