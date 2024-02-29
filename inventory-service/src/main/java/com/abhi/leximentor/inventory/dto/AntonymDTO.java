package com.abhi.leximentor.inventory.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@ToString
public class AntonymDTO {
    private String antonymKey;
    private String wordKey;
    private String word;
    private String antonym;
    private String status;
    private String source;

}
