package com.abhi.leximentor.inventory.service.impl;

import com.abhi.leximentor.inventory.constants.ExceptionMessageConstants;
import com.abhi.leximentor.inventory.constants.PartsOfSpeech;
import com.abhi.leximentor.inventory.constants.Status;
import com.abhi.leximentor.inventory.constants.WordLanguage;
import com.abhi.leximentor.inventory.dto.*;
import com.abhi.leximentor.inventory.entities.*;
import com.abhi.leximentor.inventory.exceptions.entities.ServerException;
import com.abhi.leximentor.inventory.repository.LanguageRepository;
import com.abhi.leximentor.inventory.repository.WordRepository;
import com.abhi.leximentor.inventory.util.CollectionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ServiceImplUtil {
    private final LanguageRepository languageRepository;
    private final WordRepository wordRepository;
    private final CollectionUtil collectionUtil;

    class SynonymUtil {
        public Synonym buildSynonymEntity(SynonymDTO dto, WordMetadata wordMetadata) throws ServerException.EntityObjectNotFound {
            WordMetadata synonym = wordRepository.findByKey(dto.getSynonym());
            if (synonym == null)
                throw new ServerException().new EntityObjectNotFound(ExceptionMessageConstants.WordMetadata.WORD_NOT_FOUND);
            return Synonym.builder()
                    .wordId(wordMetadata)
                    .key(UUID.randomUUID().toString())
                    .synonymWordId(synonym.getId())
                    .synonym(synonym.getWord())
                    .build();

        }

        public SynonymDTO generateSynonymWrapper(Synonym synonym) {
            return SynonymDTO.builder()
                    .synonymKey(synonym.getKey())
                    .wordKey(synonym.getWordId().getKey())
                    .word(synonym.getWordId().getWord())
                    .synonym(synonym.getSynonym())
                    .build();
        }
    }

    class AntonymUtil {
        public Antonym buildAntonymEntity(AntonymDTO dto, WordMetadata wordMetadata) throws ServerException.EntityObjectNotFound {
            WordMetadata antonym = wordRepository.findByKey(dto.getAntonym());
            if (antonym == null)
                throw new ServerException().new EntityObjectNotFound(ExceptionMessageConstants.WordMetadata.WORD_NOT_FOUND);
            return Antonym.builder()
                    .wordId(wordMetadata)
                    .key(UUID.randomUUID().toString())
                    .antonymWordId(antonym.getId())
                    .antonym(antonym.getWord())
                    .build();
        }

        public AntonymDTO generateAntonymWrapper(Antonym antonym) {
            return AntonymDTO.builder()
                    .antonymKey(antonym.getKey())
                    .wordKey(antonym.getWordId().getKey())
                    .word(antonym.getWordId().getWord())
                    .antonym(antonym.getAntonym())
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
                    .build();
        }

        public ExampleDTO generateExampleWrapper(Example example) {
            return ExampleDTO.builder()
                    .exampleKey(example.getKey())
                    .wordKey(example.getWordId().getKey())
                    .example(example.getExample())
                    .build();
        }
    }

    class WordMetadataUtil {
        public WordMetadata buildWordEntity(WordDTO dto) {
            WordMetadata wordMetadata = WordMetadata.builder()
                    .key(UUID.randomUUID().toString())
                    .word(dto.getWord())
                    .pos(getPos(dto.getPos()))
                    .pronunciation(dto.getPronunciation())
                    .language(languageRepository.findByLanguage(dto.getLanguage()))
                    .status(Status.ACTIVE)
                    .category(dto.getCategory())
                    .build();
            if (collectionUtil.isNotEmpty(dto.getSynonyms()))
                wordMetadata.setSynonyms(dto.getSynonyms().stream().map(syn -> new SynonymUtil().buildSynonymEntity(syn, wordMetadata)).collect(Collectors.toList()));
            if (collectionUtil.isNotEmpty(dto.getAntonyms()))
                wordMetadata.setAntonyms(dto.getAntonyms().stream().map(ant -> new AntonymUtil().buildAntonymEntity(ant, wordMetadata)).collect(Collectors.toList()));
            if (collectionUtil.isNotEmpty(dto.getMeanings()))
                wordMetadata.setMeanings(dto.getMeanings().stream().map(mean -> new MeaningUtil().buildMeaningEntity(mean, wordMetadata)).collect(Collectors.toList()));
            if (collectionUtil.isNotEmpty(dto.getExamples()))
                wordMetadata.setExamples(dto.getExamples().stream().map(example -> new ExampleUtil().buildExampleEntity(example, wordMetadata)).collect(Collectors.toList()));
            return wordMetadata;
        }

        public WordDTO generateWordWrapper(WordMetadata wordMetadata) {
            return WordDTO.builder()
                    .wordKey(wordMetadata.getKey())
                    .word(wordMetadata.getWord())
                    .language(wordMetadata.getLanguage().getLanguage())
                    .crtnDate(wordMetadata.getCrtnDate().toLocalDate())
                    .lastUpdDate(wordMetadata.getLastUpdDate().toLocalDate())
                    .pos(wordMetadata.getPos().name())
                    .status(Status.getStatus(wordMetadata.getStatus()))
                    .pronunciation(wordMetadata.getPronunciation())
                    .meanings(wordMetadata.getMeanings().stream().map(mean -> new MeaningUtil().generateMeaningWrapper(mean)).collect(Collectors.toList()))
                    .synonyms(wordMetadata.getSynonyms().stream().map(syn -> new SynonymUtil().generateSynonymWrapper(syn)).collect(Collectors.toList()))
                    .antonyms(wordMetadata.getAntonyms().stream().map(ant -> new AntonymUtil().generateAntonymWrapper(ant)).collect(Collectors.toList()))
                    .examples(wordMetadata.getExamples().stream().map(example -> new ExampleUtil().generateExampleWrapper(example)).collect(Collectors.toList()))
                    .category(wordMetadata.getCategory())
                    .build();
        }
    }

    protected PartsOfSpeech getPos(String pos) {
        if (pos.equalsIgnoreCase(PartsOfSpeech.ADJECTIVE.toString()))
            return PartsOfSpeech.ADJECTIVE;
        else if (pos.equalsIgnoreCase(PartsOfSpeech.ADVERB.toString()))
            return PartsOfSpeech.ADVERB;
        else if (pos.equalsIgnoreCase(PartsOfSpeech.VERB.toString()))
            return PartsOfSpeech.VERB;
        else if (pos.equalsIgnoreCase(PartsOfSpeech.CONJUNCTION.toString()))
            return PartsOfSpeech.CONJUNCTION;
        else if (pos.equalsIgnoreCase(PartsOfSpeech.INTERJECTION.toString()))
            return PartsOfSpeech.INTERJECTION;
        else if (pos.equalsIgnoreCase(PartsOfSpeech.PREPOSITION.toString()))
            return PartsOfSpeech.PREPOSITION;
        else if (pos.equalsIgnoreCase(PartsOfSpeech.NOUN.toString()))
            return PartsOfSpeech.NOUN;
        else
            return PartsOfSpeech.PRONOUN;
    }

}
