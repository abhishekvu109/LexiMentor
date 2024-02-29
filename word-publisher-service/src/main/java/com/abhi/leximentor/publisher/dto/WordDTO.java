package com.abhi.leximentor.publisher.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
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
