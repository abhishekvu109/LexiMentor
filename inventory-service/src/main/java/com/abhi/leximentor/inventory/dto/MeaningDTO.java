package com.abhi.leximentor.inventory.dto;


import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.Collection;

@Builder
@Data
@ToString
public class MeaningDTO {
    private String meaningKey;
    private String wordKey;
    private String word;
    private String meaning;
    private String source;
}
