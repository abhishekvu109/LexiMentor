package com.abhi.leximentor.inventory.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Collection;

@Builder
@Data
@ToString
public class WordDTO {
    private String wordKey;
    private String word;
    private String language;
    private LocalDate crtnDate;
    private LocalDate lastUpdDate;
    private String pos;
    private String status;
    private String pronunciation;
    private Collection<MeaningDTO> meanings;
    private Collection<SynonymDTO> synonyms;
    private Collection<AntonymDTO> antonyms;
    private Collection<ExampleDTO> examples;
    private String category;
}
