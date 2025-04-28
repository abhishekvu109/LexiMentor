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
        return wordMetadataRepository.findCountOfWordsByPos(PartsOfSpeechConstants.NOUN.name());
    }

    @Override
    public int getCountOfVerb() {
        return wordMetadataRepository.findCountOfWordsByPos(PartsOfSpeechConstants.VERB.name());
    }

    @Override
    public int getCountOfAdjective() {
        return wordMetadataRepository.findCountOfWordsByPos(PartsOfSpeechConstants.ADJECTIVE.name());

    }

    @Override
    public int getCountOfPreposition() {
        return wordMetadataRepository.findCountOfWordsByPos(PartsOfSpeechConstants.PREPOSITION.name());

    }

    @Override
    public int getCountOfAdverb() {
        return wordMetadataRepository.findCountOfWordsByPos(PartsOfSpeechConstants.ADVERB.name());
    }

    @Override
    public int getCountOfInterjection() {
        return wordMetadataRepository.findCountOfWordsByPos(PartsOfSpeechConstants.INTERJECTION.name());
    }

    @Override
    public int getCountOfPronoun() {
        return wordMetadataRepository.findCountOfWordsByPos(PartsOfSpeechConstants.PRONOUN.name());
    }

    @Override
    public int getCountOfConjunction() {
        return wordMetadataRepository.findCountOfWordsByPos(PartsOfSpeechConstants.CONJUNCTION.name());
    }
}
