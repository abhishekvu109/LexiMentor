package com.abhi.leximentor.publisher.dto;


import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@ToString
public class SynonymDTO {
    private String word;
    private String synonym;
    private String source;

}
