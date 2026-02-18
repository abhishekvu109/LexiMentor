package com.abhi.leximentor.leximentor.mapper;

import com.abhi.leximentor.leximentor.constants.Status;
import com.abhi.leximentor.leximentor.dto.inv.*;
import com.abhi.leximentor.leximentor.entities.inv.*;
import com.abhi.leximentor.leximentor.repository.inv.LanguageRepository;
import com.abhi.leximentor.leximentor.repository.inv.WordMetadataRepository;
import com.abhi.leximentor.leximentor.util.CollectionUtil;
import com.abhi.leximentor.leximentor.util.KeyGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InventoryDomainMapper {
    private final WordMetadataRepository wordMetadataRepository;
    private final LanguageRepository languageRepository;

    public Synonym toEntity(SynonymDTO dto, WordMetadata wordMetadata) {
        return Synonym.builder().wordId(wordMetadata).uuid(KeyGeneratorUtil.uuid()).refId(KeyGeneratorUtil.refId()).synonym(dto.getSynonym()).source(dto.getSource()).build();
    }

    public SynonymDTO toDto(Synonym synonym) {
        return SynonymDTO.builder().refId(String.valueOf(synonym.getRefId())).wordRefId(String.valueOf(synonym.getWordId().getRefId())).word(synonym.getWordId().getWord()).synonym(synonym.getSynonym()).source(synonym.getSource()).build();
    }

    public Antonym toEntity(AntonymDTO dto, WordMetadata wordMetadata) {
        return Antonym.builder().wordId(wordMetadata).uuid(KeyGeneratorUtil.uuid()).refId(KeyGeneratorUtil.refId()).antonym(dto.getAntonym()).source(dto.getSource()).build();
    }

    public AntonymDTO toDto(Antonym antonym) {
        return AntonymDTO.builder().refId(String.valueOf(antonym.getRefId())).word(antonym.getWordId().getWord()).antonym(antonym.getAntonym()).source(antonym.getSource()).build();
    }

    public Meaning toEntity(MeaningDTO dto, WordMetadata wordMetadata) {
        return Meaning.builder().wordId(wordMetadata).definition(dto.getMeaning()).refId(KeyGeneratorUtil.refId()).source(dto.getSource()).uuid(KeyGeneratorUtil.uuid()).build();
    }

    public MeaningDTO toDto(Meaning meaning) {
        return MeaningDTO.builder().refId(String.valueOf(meaning.getRefId())).wordRefId(String.valueOf(meaning.getWordId().getRefId())).word(meaning.getWordId().getWord()).source(meaning.getSource()).meaning(meaning.getDefinition()).build();
    }

    public Example toEntity(ExampleDTO dto, WordMetadata wordMetadata) {
        return Example.builder().wordId(wordMetadata).refId(KeyGeneratorUtil.refId()).example(dto.getExample()).uuid(KeyGeneratorUtil.uuid()).source(dto.getSource()).build();
    }

    public ExampleDTO toDto(Example example) {
        return ExampleDTO.builder().refId(String.valueOf(example.getRefId())).word(example.getWordId().getWord()).wordRefId(String.valueOf(example.getWordId().getRefId())).example(example.getExample()).source(example.getSource()).build();
    }

    public PartsOfSpeech toEntity(PartsOfSpeechDTO dto, WordMetadata wordMetadata) {
        return PartsOfSpeech.builder().wordId(wordMetadata).refId(KeyGeneratorUtil.refId()).uuid(KeyGeneratorUtil.uuid()).pos(dto.getPos()).source(dto.getSource()).build();
    }

    public PartsOfSpeechDTO toDto(PartsOfSpeech partsOfSpeech) {
        return PartsOfSpeechDTO.builder().wordRefId(String.valueOf(partsOfSpeech.getWordId().getRefId())).word(partsOfSpeech.getWordId().getWord()).pos(partsOfSpeech.getPos()).source(partsOfSpeech.getSource()).build();
    }

    public WordMetadata toEntity(WordDTO dto) {
        WordMetadata wordMetadata = wordMetadataRepository.findByWord(dto.getWord().toUpperCase());
        if (wordMetadata == null) {
            return buildNewObject(dto);
        }
        return buildExistingObject(wordMetadata, dto);
    }

    public WordDTO toDto(WordMetadata wordMetadata) {
        return WordDTO.builder().refId(String.valueOf(wordMetadata.getRefId())).word(wordMetadata.getWord()).localMeaning(wordMetadata.getLocalMeaning()).mnemonic(wordMetadata.getMnemonic()).language(wordMetadata.getLanguage().getLanguage()).crtnDate(wordMetadata.getCrtnDate().toLocalDate()).lastUpdDate(wordMetadata.getLastUpdDate().toLocalDate()).pos(wordMetadata.getPos()).status(Status.ApplicationStatus.getStatusStr(wordMetadata.getStatus())).pronunciation(wordMetadata.getPronunciation()).partsOfSpeeches(CollectionUtil.isNotEmpty(wordMetadata.getPartsOfSpeeches()) ? wordMetadata.getPartsOfSpeeches().stream().map(this::toDto).collect(Collectors.toList()) : null).meanings(CollectionUtil.isNotEmpty(wordMetadata.getMeanings()) ? wordMetadata.getMeanings().stream().map(this::toDto).collect(Collectors.toList()) : null).synonyms(CollectionUtil.isNotEmpty(wordMetadata.getSynonyms()) ? wordMetadata.getSynonyms().stream().map(this::toDto).collect(Collectors.toList()) : null).antonyms(CollectionUtil.isNotEmpty(wordMetadata.getAntonyms()) ? wordMetadata.getAntonyms().stream().map(this::toDto).collect(Collectors.toList()) : null).examples(CollectionUtil.isNotEmpty(wordMetadata.getExamples()) ? wordMetadata.getExamples().stream().map(this::toDto).collect(Collectors.toList()) : null).category(wordMetadata.getCategory()).source(wordMetadata.getSource()).build();
    }

    private WordMetadata buildNewObject(WordDTO dto) {
        WordMetadata wordMetadata = WordMetadata.builder().refId(KeyGeneratorUtil.refId()).uuid(KeyGeneratorUtil.uuid()).word(dto.getWord()).pos(dto.getPos()).pronunciation(dto.getPronunciation()).language(languageRepository.findByLanguage(dto.getLanguage())).status(Status.ApplicationStatus.ACTIVE).source(dto.getSource()).category(dto.getCategory()).localMeaning(dto.getLocalMeaning()).mnemonic(dto.getMnemonic()).build();
        if (CollectionUtil.isNotEmpty(dto.getPartsOfSpeeches()))
            wordMetadata.setPartsOfSpeeches(dto.getPartsOfSpeeches().stream().map(pos -> toEntity(pos, wordMetadata)).collect(Collectors.toList()));
        if (CollectionUtil.isNotEmpty(dto.getSynonyms()))
            wordMetadata.setSynonyms(dto.getSynonyms().stream().map(syn -> toEntity(syn, wordMetadata)).collect(Collectors.toList()));
        if (CollectionUtil.isNotEmpty(dto.getAntonyms()))
            wordMetadata.setAntonyms(dto.getAntonyms().stream().map(ant -> toEntity(ant, wordMetadata)).collect(Collectors.toList()));
        if (CollectionUtil.isNotEmpty(dto.getMeanings()))
            wordMetadata.setMeanings(dto.getMeanings().stream().map(mean -> toEntity(mean, wordMetadata)).collect(Collectors.toList()));
        if (CollectionUtil.isNotEmpty(dto.getExamples()))
            wordMetadata.setExamples(dto.getExamples().stream().map(example -> toEntity(example, wordMetadata)).collect(Collectors.toList()));
        return wordMetadata;
    }

    private WordMetadata buildExistingObject(WordMetadata wordMetadata, WordDTO dto) {
        if (StringUtils.isEmpty(wordMetadata.getPronunciation()))
            wordMetadata.setPronunciation(dto.getPronunciation());
        if (StringUtils.isEmpty(wordMetadata.getMnemonic())) wordMetadata.setMnemonic(dto.getMnemonic());
        if (StringUtils.isEmpty(wordMetadata.getLocalMeaning()))
            wordMetadata.setLocalMeaning(dto.getLocalMeaning());
        if (StringUtils.isEmpty(wordMetadata.getCategory())) wordMetadata.setCategory(dto.getCategory());
        List<Meaning> meanings = CollectionUtil.isEmpty(wordMetadata.getMeanings()) ? new LinkedList<>() : wordMetadata.getMeanings();
        List<Example> examples = CollectionUtil.isEmpty(wordMetadata.getExamples()) ? new LinkedList<>() : wordMetadata.getExamples();
        List<Synonym> synonyms = CollectionUtil.isEmpty(wordMetadata.getSynonyms()) ? new LinkedList<>() : wordMetadata.getSynonyms();
        List<Antonym> antonyms = CollectionUtil.isEmpty(wordMetadata.getAntonyms()) ? new LinkedList<>() : wordMetadata.getAntonyms();
        List<PartsOfSpeech> partsOfSpeeches = CollectionUtil.isEmpty(wordMetadata.getPartsOfSpeeches()) ? new LinkedList<>() : wordMetadata.getPartsOfSpeeches();
        if (CollectionUtil.isNotEmpty(dto.getMeanings()))
            meanings.addAll(dto.getMeanings().stream().map(m -> toEntity(m, wordMetadata)).toList());
        if (CollectionUtil.isNotEmpty(dto.getExamples()))
            examples.addAll(dto.getExamples().stream().map(ex -> toEntity(ex, wordMetadata)).toList());
        if (CollectionUtil.isNotEmpty(dto.getSynonyms()))
            synonyms.addAll(dto.getSynonyms().stream().map(syn -> toEntity(syn, wordMetadata)).toList());
        if (CollectionUtil.isNotEmpty(dto.getAntonyms()))
            antonyms.addAll(dto.getAntonyms().stream().map(ant -> toEntity(ant, wordMetadata)).toList());
        if (CollectionUtil.isNotEmpty(dto.getPartsOfSpeeches()))
            partsOfSpeeches.addAll(dto.getPartsOfSpeeches().stream().map(pos -> toEntity(pos, wordMetadata)).toList());
        wordMetadata.setMeanings(meanings);
        wordMetadata.setExamples(examples);
        wordMetadata.setSynonyms(synonyms);
        wordMetadata.setAntonyms(antonyms);
        wordMetadata.setPartsOfSpeeches(partsOfSpeeches);
        return wordMetadata;
    }
}
