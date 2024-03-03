package com.abhi.leximentor.inventory.dto.other;

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
    public String Category;
    public String Pronunciation;
    public String Word;
}
