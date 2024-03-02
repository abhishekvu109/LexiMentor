package com.abhi.leximentor.inventory.dto.kafka;


import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@ToString
public class MeaningDTO {
    private String word;
    private String meaning;
    private String source;

}
