package com.abhi.leximentor.inventory.dto.inv;

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

    private long refId;
    private String word;
    private String source;
    private String language;
    private LocalDate crtnDate;
    private LocalDate lastUpdDate;

    @NotEmpty(message = "Cannot be null")
    @NotBlank(message = "cannot be blank")
    private String pos;
    private String status;
    private String pronunciation;
    private Collection<MeaningDTO> meanings;
    private Collection<SynonymDTO> synonyms;
    private Collection<AntonymDTO> antonyms;
    private Collection<ExampleDTO> examples;
    private Collection<PartsOfSpeechDTO> partsOfSpeeches;
    private String category;
}
