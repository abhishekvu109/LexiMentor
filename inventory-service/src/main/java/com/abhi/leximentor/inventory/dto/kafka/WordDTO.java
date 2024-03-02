package com.abhi.leximentor.inventory.dto.kafka;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.Collection;

@Builder
@Data
@ToString
public class WordDTO {

    private String word;
    private String language;
    private String source;
    private String pos;
    private String pronunciation;
    private Collection<MeaningDTO> meanings;
    private Collection<SynonymDTO> synonyms;
    private Collection<AntonymDTO> antonyms;
    private Collection<ExampleDTO> examples;
}
