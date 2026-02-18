package com.abhi.leximentor.leximentor.dto.inv;


import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@ToString
public class MeaningDTO {
    private String refId;
    private String wordRefId;
    private String word;
    private String meaning;
    private String source;
}
