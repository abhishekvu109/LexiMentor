package com.abhi.leximentor.inventory.service.analytics.impl;

import com.abhi.leximentor.inventory.constants.PartsOfSpeechConstants;
import com.abhi.leximentor.inventory.repository.inv.WordMetadataRepository;
import com.abhi.leximentor.inventory.service.analytics.WordMetadataAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WordMetadataAnalyticsServiceImpl implements WordMetadataAnalyticsService {
    private final WordMetadataRepository wordMetadataRepository;

    @Override
    public int getCountOfNoun() {
        log.info("Counting nouns");
        return wordMetadataRepository.findCountOfWordsByPos(PartsOfSpeechConstants.NOUN.name());
    }

    @Override
    public int getCountOfVerb() {
        log.info("Counting verbs");
        return wordMetadataRepository.findCountOfWordsByPos(PartsOfSpeechConstants.VERB.name());
    }

    @Override
    public int getCountOfAdjective() {
        log.info("Counting adjectives");
        return wordMetadataRepository.findCountOfWordsByPos(PartsOfSpeechConstants.ADJECTIVE.name());

    }

    @Override
    public int getCountOfPreposition() {
        log.info("Counting prepositions");
        return wordMetadataRepository.findCountOfWordsByPos(PartsOfSpeechConstants.PREPOSITION.name());

    }

    @Override
    public int getCountOfAdverb() {
        log.info("Counting adverbs");
        return wordMetadataRepository.findCountOfWordsByPos(PartsOfSpeechConstants.ADVERB.name());
    }

    @Override
    public int getCountOfInterjection() {
        log.info("Counting interjections");
        return wordMetadataRepository.findCountOfWordsByPos(PartsOfSpeechConstants.INTERJECTION.name());
    }

    @Override
    public int getCountOfPronoun() {
        log.info("Counting pronouns");
        return wordMetadataRepository.findCountOfWordsByPos(PartsOfSpeechConstants.PRONOUN.name());
    }

    @Override
    public int getCountOfConjunction() {
        log.info("Counting conjunctions");
        return wordMetadataRepository.findCountOfWordsByPos(PartsOfSpeechConstants.CONJUNCTION.name());
    }
}
