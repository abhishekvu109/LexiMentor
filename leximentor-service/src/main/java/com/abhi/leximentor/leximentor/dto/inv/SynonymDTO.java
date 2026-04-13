package com.abhi.leximentor.leximentor.dto.inv;


import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@ToString
public class SynonymDTO {
    private String key;
    private String wordKey;
    private String word;
    private String synonym;
    private String source;

}
