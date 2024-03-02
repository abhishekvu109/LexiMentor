package com.abhi.leximentor.inventory.dto.kafka;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@ToString
public class ExampleDTO {
    private String word;
    private String example;
    private String source;

}
