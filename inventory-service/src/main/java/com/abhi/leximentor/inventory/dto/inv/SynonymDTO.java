package com.abhi.leximentor.inventory.dto.inv;


import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@ToString
public class SynonymDTO {
    private String refId;
    private String wordRefId;
    private String word;
    private String synonym;
    private String source;

}
