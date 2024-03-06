package com.abhi.leximentor.inventory.dto.inv;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@ToString
public class ExampleDTO {
    private String exampleKey;
    private String wordKey;
    private String example;
    private String source;

}
