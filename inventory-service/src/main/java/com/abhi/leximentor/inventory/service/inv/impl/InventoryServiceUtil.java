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
public class InventoryServiceUtil {

    static class SynonymUtil {
        public static Synonym buildEntity(SynonymDTO dto, WordMetadata wordMetadata) {
            return Synonym.builder().wordId(wordMetadata).key(UUID.randomUUID().toString()).synonym(dto.getSynonym()).source(dto.getSource()).build();

        }

        public static SynonymDTO buildDTO(Synonym synonym) {
            return SynonymDTO.builder().synonymKey(synonym.getKey()).wordKey(synonym.getWordId().getKey()).word(synonym.getWordId().getWord()).synonym(synonym.getSynonym()).source(synonym.getSource()).build();
        }
    }

    static class AntonymUtil {
        public static Antonym buildEntity(AntonymDTO dto, WordMetadata wordMetadata) {
            return Antonym.builder().wordId(wordMetadata).key(UUID.randomUUID().toString()).antonym(dto.getAntonym()).source(dto.getSource()).build();
        }

        public static AntonymDTO buildDTO(Antonym antonym) {
            return AntonymDTO.builder().antonymKey(antonym.getKey()).word(antonym.getWordId().getWord()).antonym(antonym.getAntonym()).source(antonym.getSource()).build();
        }
    }

    static class MeaningUtil {
        public static Meaning buildEntity(MeaningDTO dto, WordMetadata wordMetadata) {
            return Meaning.builder().wordId(wordMetadata).definition(dto.getMeaning()).source(dto.getSource()).key(UUID.randomUUID().toString()).build();
        }

        public static MeaningDTO buildDTO(Meaning meaning) {
            return MeaningDTO.builder().meaningKey(meaning.getKey()).wordKey(meaning.getWordId().getKey()).word(meaning.getWordId().getWord()).source(meaning.getSource()).meaning(meaning.getDefinition()).build();
        }
    }

    static class ExampleUtil {
        public static Example buildEntity(ExampleDTO dto, WordMetadata wordMetadata) {
            return Example.builder().wordId(wordMetadata).example(dto.getExample()).key(UUID.randomUUID().toString()).source(dto.getSource()).build();
        }

        public static ExampleDTO buildDTO(Example example) {
            return ExampleDTO.builder().exampleKey(example.getKey()).wordKey(example.getWordId().getKey()).example(example.getExample()).source(example.getSource()).build();
        }
    }

    static class PartsOfSpeechUtil {
        public static PartsOfSpeech buildEntity(PartsOfSpeechDTO dto, WordMetadata wordMetadata) {
            return PartsOfSpeech.builder().wordId(wordMetadata).pos(dto.getPos()).key(dto.getRefId()).source(dto.getSource()).build();
        }

        public static PartsOfSpeechDTO buildDTO(PartsOfSpeech partsOfSpeech) {
            return PartsOfSpeechDTO.builder().wordRefId(partsOfSpeech.getWordId().getKey()).word(partsOfSpeech.getWordId().getWord()).pos(partsOfSpeech.getPos()).source(partsOfSpeech.getSource()).build();
        }
    }

    static class WordMetadataUtil {
        private static WordMetadata buildNewObject(WordDTO dto, LanguageRepository languageRepository) {
            WordMetadata wordMetadata = WordMetadata.builder().key(UUID.randomUUID().toString()).word(dto.getWord()).pos(dto.getPos()).pronunciation(dto.getPronunciation()).language(languageRepository.findByLanguage(dto.getLanguage())).status(Status.ACTIVE).source(dto.getSource()).category(dto.getCategory()).build();
            if (CollectionUtil.isNotEmpty(dto.getPartsOfSpeeches()))
                wordMetadata.setPartsOfSpeeches(dto.getPartsOfSpeeches().stream().map(pos -> new InventoryServiceUtil.PartsOfSpeechUtil().buildEntity(pos, wordMetadata)).collect(Collectors.toList()));
            if (CollectionUtil.isNotEmpty(dto.getSynonyms()))
                wordMetadata.setSynonyms(dto.getSynonyms().stream().map(syn -> new InventoryServiceUtil.SynonymUtil().buildEntity(syn, wordMetadata)).collect(Collectors.toList()));
            if (CollectionUtil.isNotEmpty(dto.getAntonyms()))
                wordMetadata.setAntonyms(dto.getAntonyms().stream().map(ant -> new InventoryServiceUtil.AntonymUtil().buildEntity(ant, wordMetadata)).collect(Collectors.toList()));
            if (CollectionUtil.isNotEmpty(dto.getMeanings()))
                wordMetadata.setMeanings(dto.getMeanings().stream().map(mean -> new InventoryServiceUtil.MeaningUtil().buildEntity(mean, wordMetadata)).collect(Collectors.toList()));
            if (CollectionUtil.isNotEmpty(dto.getExamples()))
                wordMetadata.setExamples(dto.getExamples().stream().map(example -> new InventoryServiceUtil.ExampleUtil().buildEntity(example, wordMetadata)).collect(Collectors.toList()));
            return wordMetadata;
        }

        public static WordMetadata buildExistingObject(WordMetadata wordMetadata, WordDTO dto) {
            List<Meaning> meanings = CollectionUtil.isEmpty(wordMetadata.getMeanings()) ? new LinkedList<>() : wordMetadata.getMeanings();
            List<Example> examples = CollectionUtil.isEmpty(wordMetadata.getExamples()) ? new LinkedList<>() : wordMetadata.getExamples();
            List<Synonym> synonyms = CollectionUtil.isEmpty(wordMetadata.getSynonyms()) ? new LinkedList<>() : wordMetadata.getSynonyms();
            List<Antonym> antonyms = CollectionUtil.isEmpty(wordMetadata.getAntonyms()) ? new LinkedList<>() : wordMetadata.getAntonyms();
            List<PartsOfSpeech> partsOfSpeeches = CollectionUtil.isEmpty(wordMetadata.getPartsOfSpeeches()) ? new LinkedList<>() : wordMetadata.getPartsOfSpeeches();
            if (CollectionUtil.isNotEmpty(dto.getMeanings()))
                meanings.addAll(dto.getMeanings().stream().map(m -> new InventoryServiceUtil.MeaningUtil().buildEntity(m, wordMetadata)).toList());
            if (CollectionUtil.isNotEmpty(dto.getExamples()))
                examples.addAll(dto.getExamples().stream().map(ex -> new InventoryServiceUtil.ExampleUtil().buildEntity(ex, wordMetadata)).toList());
            if (CollectionUtil.isNotEmpty(dto.getSynonyms()))
                synonyms.addAll(dto.getSynonyms().stream().map(syn -> new InventoryServiceUtil.SynonymUtil().buildEntity(syn, wordMetadata)).toList());
            if (CollectionUtil.isNotEmpty(dto.getAntonyms()))
                antonyms.addAll(dto.getAntonyms().stream().map(ant -> new InventoryServiceUtil.AntonymUtil().buildEntity(ant, wordMetadata)).toList());
            if (CollectionUtil.isNotEmpty(dto.getPartsOfSpeeches()))
                partsOfSpeeches.addAll(dto.getPartsOfSpeeches().stream().map(pos -> new InventoryServiceUtil.PartsOfSpeechUtil().buildEntity(pos, wordMetadata)).toList());
            wordMetadata.setMeanings(meanings);
            wordMetadata.setExamples(examples);
            wordMetadata.setSynonyms(synonyms);
            wordMetadata.setAntonyms(antonyms);
            wordMetadata.setPartsOfSpeeches(partsOfSpeeches);
            return wordMetadata;
        }

        public static WordMetadata buildEntity(WordDTO dto, WordMetadataRepository wordMetadataRepository, LanguageRepository languageRepository) {
            WordMetadata wordMetadata = wordMetadataRepository.findByWord(dto.getWord().toUpperCase());
            if (wordMetadata == null) return buildNewObject(dto, languageRepository);
            else return buildExistingObject(wordMetadata, dto);
        }

        public static WordDTO buildDTO(WordMetadata wordMetadata) {
            return WordDTO.builder().wordKey(wordMetadata.getKey()).word(wordMetadata.getWord()).language(wordMetadata.getLanguage().getLanguage()).crtnDate(wordMetadata.getCrtnDate().toLocalDate()).lastUpdDate(wordMetadata.getLastUpdDate().toLocalDate()).pos(wordMetadata.getPos()).status(Status.getStatus(wordMetadata.getStatus())).pronunciation(wordMetadata.getPronunciation()).partsOfSpeeches(CollectionUtil.isNotEmpty(wordMetadata.getPartsOfSpeeches()) ? wordMetadata.getPartsOfSpeeches().stream().map(pos -> new InventoryServiceUtil.PartsOfSpeechUtil().buildDTO(pos)).collect(Collectors.toList()) : null).meanings(CollectionUtil.isNotEmpty(wordMetadata.getMeanings()) ? wordMetadata.getMeanings().stream().map(mean -> new InventoryServiceUtil.MeaningUtil().buildDTO(mean)).collect(Collectors.toList()) : null).synonyms(CollectionUtil.isNotEmpty(wordMetadata.getSynonyms()) ? wordMetadata.getSynonyms().stream().map(syn -> new InventoryServiceUtil.SynonymUtil().buildDTO(syn)).collect(Collectors.toList()) : null).antonyms(CollectionUtil.isNotEmpty(wordMetadata.getAntonyms()) ? wordMetadata.getAntonyms().stream().map(ant -> new InventoryServiceUtil.AntonymUtil().buildDTO(ant)).collect(Collectors.toList()) : null).examples(CollectionUtil.isNotEmpty(wordMetadata.getExamples()) ? wordMetadata.getExamples().stream().map(example -> new InventoryServiceUtil.ExampleUtil().buildDTO(example)).collect(Collectors.toList()) : null).category(wordMetadata.getCategory()).source(wordMetadata.getSource()).build();
        }
    }

}
