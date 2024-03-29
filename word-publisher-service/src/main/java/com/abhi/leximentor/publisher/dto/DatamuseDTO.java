package com.abhi.leximentor.publisher.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@ToString
public class DatamuseDTO {
    public List<String> Antonyms;
    public List<String> Synonyms;
    public List<String> Examples;
    public String Pos;
}
