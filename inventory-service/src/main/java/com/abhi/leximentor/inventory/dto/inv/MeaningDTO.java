package com.abhi.leximentor.inventory.dto.inv;


import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@ToString
public class MeaningDTO {
    private long refId;
    private long wordRefId;
    private String word;
    private String meaning;
    private String source;
}
