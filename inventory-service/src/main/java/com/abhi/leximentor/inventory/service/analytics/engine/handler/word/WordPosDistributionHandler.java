package com.abhi.leximentor.inventory.service.analytics.engine.handler.word;

import com.abhi.leximentor.inventory.constants.PartsOfSpeechConstants;
import com.abhi.leximentor.inventory.repository.inv.WordMetadataRepository;
import com.abhi.leximentor.inventory.service.analytics.engine.BaseAnalyticsHandler;
import com.abhi.leximentor.inventory.service.analytics.engine.context.WordAnalyticsContext;

import java.util.LinkedHashMap;
import java.util.Map;

public class WordPosDistributionHandler extends BaseAnalyticsHandler<WordAnalyticsContext> {
    private final WordMetadataRepository wordMetadataRepository;

    public WordPosDistributionHandler(WordMetadataRepository wordMetadataRepository) {
        this.wordMetadataRepository = wordMetadataRepository;
    }

    @Override
    public void handle(WordAnalyticsContext context) {
        Map<String, Integer> distribution = new LinkedHashMap<>();
        distribution.put(PartsOfSpeechConstants.NOUN.name(), wordMetadataRepository.findCountOfWordsByPos(PartsOfSpeechConstants.NOUN.name()));
        distribution.put(PartsOfSpeechConstants.VERB.name(), wordMetadataRepository.findCountOfWordsByPos(PartsOfSpeechConstants.VERB.name()));
        distribution.put(PartsOfSpeechConstants.ADJECTIVE.name(), wordMetadataRepository.findCountOfWordsByPos(PartsOfSpeechConstants.ADJECTIVE.name()));
        distribution.put(PartsOfSpeechConstants.ADVERB.name(), wordMetadataRepository.findCountOfWordsByPos(PartsOfSpeechConstants.ADVERB.name()));
        distribution.put(PartsOfSpeechConstants.PREPOSITION.name(), wordMetadataRepository.findCountOfWordsByPos(PartsOfSpeechConstants.PREPOSITION.name()));
        distribution.put(PartsOfSpeechConstants.CONJUNCTION.name(), wordMetadataRepository.findCountOfWordsByPos(PartsOfSpeechConstants.CONJUNCTION.name()));
        distribution.put(PartsOfSpeechConstants.PRONOUN.name(), wordMetadataRepository.findCountOfWordsByPos(PartsOfSpeechConstants.PRONOUN.name()));
        distribution.put(PartsOfSpeechConstants.INTERJECTION.name(), wordMetadataRepository.findCountOfWordsByPos(PartsOfSpeechConstants.INTERJECTION.name()));
        context.setPosDistribution(distribution);
        context.getBuilder().posDistribution(distribution);
        next(context);
    }
}
