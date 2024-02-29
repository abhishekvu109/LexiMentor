package com.abhi.leximentor.publisher.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@NoArgsConstructor
public class NltkDTO {
    public List<String> Antonyms;
    public List<String> Synonyms;
    public List<String> Examples;
    public String Definition;
    public String Pos;
}
