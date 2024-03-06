package com.abhi.leximentor.inventory.service.inv.impl;

import com.abhi.leximentor.inventory.constants.Status;
import com.abhi.leximentor.inventory.dto.inv.*;
import com.abhi.leximentor.inventory.entities.inv.*;
import com.abhi.leximentor.inventory.repository.inv.LanguageRepository;
import com.abhi.leximentor.inventory.repository.inv.WordMetadataRepository;
import com.abhi.leximentor.inventory.util.CollectionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ServiceImplUtil2 {
    private final LanguageRepository languageRepository;
    private final WordMetadataRepository wordRepository;
    private final CollectionUtil collectionUtil;

    class SynonymUtil {
        public Synonym buildSynonymEntity(SynonymDTO dto, WordMetadata wordMetadata) {
            return Synonym.builder()
                    .wordId(wordMetadata)
                    .key(UUID.randomUUID().toString())
                    .synonym(dto.getSynonym())
                    .source(dto.getSource())
                    .build();

        }

        public SynonymDTO generateSynonymWrapper(Synonym synonym) {
            return SynonymDTO.builder()
                    .synonymKey(synonym.getKey())
                    .wordKey(synonym.getWordId().getKey())
                    .word(synonym.getWordId().getWord())
                    .synonym(synonym.getSynonym())
                    .source(synonym.getSource())
                    .build();
        }
    }

    class AntonymUtil {
        public Antonym buildAntonymEntity(AntonymDTO dto, WordMetadata wordMetadata) {
            return Antonym.builder()
                    .wordId(wordMetadata)
                    .key(UUID.randomUUID().toString())
                    .antonym(dto.getAntonym())
                    .source(dto.getSource())
                    .build();
        }

        public AntonymDTO generateAntonymWrapper(Antonym antonym) {
            return AntonymDTO.builder()
                    .antonymKey(antonym.getKey())
                    .word(antonym.getWordId().getWord())
                    .antonym(antonym.getAntonym())
                    .source(antonym.getSource())
                    .build();
        }
    }

    class MeaningUtil {
        public Meaning buildMeaningEntity(MeaningDTO dto, WordMetadata wordMetadata) {
            return Meaning.builder()
                    .wordId(wordMetadata)
                    .definition(dto.getMeaning())
                    .source(dto.getSource())
                    .key(UUID.randomUUID().toString())
                    .build();
        }

        public MeaningDTO generateMeaningWrapper(Meaning meaning) {
            return MeaningDTO.builder()
                    .meaningKey(meaning.getKey())
                    .wordKey(meaning.getWordId().getKey())
                    .word(meaning.getWordId().getWord())
                    .source(meaning.getSource())
                    .meaning(meaning.getDefinition())
                    .build();
        }
    }

    class ExampleUtil {
        public Example buildExampleEntity(ExampleDTO dto, WordMetadata wordMetadata) {
            return Example.builder()
                    .wordId(wordMetadata)
                    .example(dto.getExample())
                    .key(UUID.randomUUID().toString())
                    .source(dto.getSource())
                    .build();
        }

        public ExampleDTO generateExampleWrapper(Example example) {
            return ExampleDTO.builder()
                    .exampleKey(example.getKey())
                    .wordKey(example.getWordId().getKey())
                    .example(example.getExample())
                    .source(example.getSource())
                    .build();
        }
    }

    class PartsOfSpeechUtil {
        public PartsOfSpeech buildPartsOfSpeechEntity(PartsOfSpeechDTO dto, WordMetadata wordMetadata) {
            return PartsOfSpeech.builder()
                    .wordId(wordMetadata)
                    .pos(dto.getPos())
                    .key(dto.getRefId())
                    .source(dto.getSource())
                    .build();
        }

        public PartsOfSpeechDTO generatePartsOfSpeechWrapper(PartsOfSpeech partsOfSpeech) {
            return PartsOfSpeechDTO.builder()
                    .wordRefId(partsOfSpeech.getWordId().getKey())
                    .word(partsOfSpeech.getWordId().getWord())
                    .pos(partsOfSpeech.getPos())
                    .source(partsOfSpeech.getSource())
                    .build();
        }
    }

    class WordMetadataUtil {
        private WordMetadata buildNewObject(WordDTO dto) {
            WordMetadata wordMetadata = WordMetadata.builder()
                    .key(UUID.randomUUID().toString())
                    .word(dto.getWord())
                    .pos(dto.getPos())
                    .pronunciation(dto.getPronunciation())
                    .language(languageRepository.findByLanguage(dto.getLanguage()))
                    .status(Status.ACTIVE)
                    .source(dto.getSource())
                    .category(dto.getCategory())
                    .build();
            if (collectionUtil.isNotEmpty(dto.getPartsOfSpeeches()))
                wordMetadata.setPartsOfSpeeches(dto.getPartsOfSpeeches().stream().map(pos -> new ServiceImplUtil2.PartsOfSpeechUtil().buildPartsOfSpeechEntity(pos, wordMetadata)).collect(Collectors.toList()));
            if (collectionUtil.isNotEmpty(dto.getSynonyms()))
                wordMetadata.setSynonyms(dto.getSynonyms().stream().map(syn -> new ServiceImplUtil2.SynonymUtil().buildSynonymEntity(syn, wordMetadata)).collect(Collectors.toList()));
            if (collectionUtil.isNotEmpty(dto.getAntonyms()))
                wordMetadata.setAntonyms(dto.getAntonyms().stream().map(ant -> new ServiceImplUtil2.AntonymUtil().buildAntonymEntity(ant, wordMetadata)).collect(Collectors.toList()));
            if (collectionUtil.isNotEmpty(dto.getMeanings()))
                wordMetadata.setMeanings(dto.getMeanings().stream().map(mean -> new ServiceImplUtil2.MeaningUtil().buildMeaningEntity(mean, wordMetadata)).collect(Collectors.toList()));
            if (collectionUtil.isNotEmpty(dto.getExamples()))
                wordMetadata.setExamples(dto.getExamples().stream().map(example -> new ServiceImplUtil2.ExampleUtil().buildExampleEntity(example, wordMetadata)).collect(Collectors.toList()));
            return wordMetadata;
        }

        public WordMetadata buildExistingObject(WordMetadata wordMetadata, WordDTO dto) {
            List<Meaning> meanings = collectionUtil.isEmpty(wordMetadata.getMeanings()) ? new LinkedList<>() : wordMetadata.getMeanings();
            List<Example> examples = collectionUtil.isEmpty(wordMetadata.getExamples()) ? new LinkedList<>() : wordMetadata.getExamples();
            List<Synonym> synonyms = collectionUtil.isEmpty(wordMetadata.getSynonyms()) ? new LinkedList<>() : wordMetadata.getSynonyms();
            List<Antonym> antonyms = collectionUtil.isEmpty(wordMetadata.getAntonyms()) ? new LinkedList<>() : wordMetadata.getAntonyms();
            List<PartsOfSpeech> partsOfSpeeches = collectionUtil.isEmpty(wordMetadata.getPartsOfSpeeches()) ? new LinkedList<>() : wordMetadata.getPartsOfSpeeches();
            if (collectionUtil.isNotEmpty(dto.getMeanings()))
                meanings.addAll(dto.getMeanings().stream().map(m -> new ServiceImplUtil2.MeaningUtil().buildMeaningEntity(m, wordMetadata)).toList());
            if (collectionUtil.isNotEmpty(dto.getExamples()))
                examples.addAll(dto.getExamples().stream().map(ex -> new ServiceImplUtil2.ExampleUtil().buildExampleEntity(ex, wordMetadata)).toList());
            if (collectionUtil.isNotEmpty(dto.getSynonyms()))
                synonyms.addAll(dto.getSynonyms().stream().map(syn -> new ServiceImplUtil2.SynonymUtil().buildSynonymEntity(syn, wordMetadata)).toList());
            if (collectionUtil.isNotEmpty(dto.getAntonyms()))
                antonyms.addAll(dto.getAntonyms().stream().map(ant -> new ServiceImplUtil2.AntonymUtil().buildAntonymEntity(ant, wordMetadata)).toList());
            if (collectionUtil.isNotEmpty(dto.getPartsOfSpeeches()))
                partsOfSpeeches.addAll(dto.getPartsOfSpeeches().stream().map(pos -> new ServiceImplUtil2.PartsOfSpeechUtil().buildPartsOfSpeechEntity(pos, wordMetadata)).toList());
            wordMetadata.setMeanings(meanings);
            wordMetadata.setExamples(examples);
            wordMetadata.setSynonyms(synonyms);
            wordMetadata.setAntonyms(antonyms);
            wordMetadata.setPartsOfSpeeches(partsOfSpeeches);
            return wordMetadata;
        }

        public WordMetadata buildWordEntity(WordDTO dto) {
            WordMetadata wordMetadata = wordRepository.findByWord(dto.getWord().toUpperCase());
            if (wordMetadata == null)
                return buildNewObject(dto);
            else
                return buildExistingObject(wordMetadata, dto);
        }

        public WordDTO generateWordWrapper(WordMetadata wordMetadata) {
            return WordDTO.builder()
                    .wordKey(wordMetadata.getKey())
                    .word(wordMetadata.getWord())
                    .language(wordMetadata.getLanguage().getLanguage())
                    .crtnDate(wordMetadata.getCrtnDate().toLocalDate())
                    .lastUpdDate(wordMetadata.getLastUpdDate().toLocalDate())
                    .pos(wordMetadata.getPos())
                    .status(Status.getStatus(wordMetadata.getStatus()))
                    .pronunciation(wordMetadata.getPronunciation())
                    .partsOfSpeeches(collectionUtil.isNotEmpty(wordMetadata.getPartsOfSpeeches()) ? wordMetadata.getPartsOfSpeeches().stream().map(pos -> new ServiceImplUtil2.PartsOfSpeechUtil().generatePartsOfSpeechWrapper(pos)).collect(Collectors.toList()) : null)
                    .meanings(collectionUtil.isNotEmpty(wordMetadata.getMeanings()) ? wordMetadata.getMeanings().stream().map(mean -> new ServiceImplUtil2.MeaningUtil().generateMeaningWrapper(mean)).collect(Collectors.toList()) : null)
                    .synonyms(collectionUtil.isNotEmpty(wordMetadata.getSynonyms()) ? wordMetadata.getSynonyms().stream().map(syn -> new ServiceImplUtil2.SynonymUtil().generateSynonymWrapper(syn)).collect(Collectors.toList()) : null)
                    .antonyms(collectionUtil.isNotEmpty(wordMetadata.getAntonyms()) ? wordMetadata.getAntonyms().stream().map(ant -> new ServiceImplUtil2.AntonymUtil().generateAntonymWrapper(ant)).collect(Collectors.toList()) : null)
                    .examples(collectionUtil.isNotEmpty(wordMetadata.getExamples()) ? wordMetadata.getExamples().stream().map(example -> new ServiceImplUtil2.ExampleUtil().generateExampleWrapper(example)).collect(Collectors.toList()) : null)
                    .category(wordMetadata.getCategory())
                    .source(wordMetadata.getSource())
                    .build();
        }
    }

}
