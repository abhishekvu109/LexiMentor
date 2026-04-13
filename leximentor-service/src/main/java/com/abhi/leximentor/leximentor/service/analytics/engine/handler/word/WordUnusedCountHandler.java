package com.abhi.leximentor.leximentor.service.analytics.engine.handler.word;

import com.abhi.leximentor.leximentor.repository.inv.WordMetadataRepository;
import com.abhi.leximentor.leximentor.service.analytics.engine.BaseAnalyticsHandler;
import com.abhi.leximentor.leximentor.service.analytics.engine.context.WordDistributionContext;

public class WordUnusedCountHandler extends BaseAnalyticsHandler<WordDistributionContext> {
    private final WordMetadataRepository wordMetadataRepository;

    public WordUnusedCountHandler(WordMetadataRepository wordMetadataRepository) {
        this.wordMetadataRepository = wordMetadataRepository;
    }

    @Override
    public void handle(WordDistributionContext context) {
        long unused = wordMetadataRepository.countUnusedWords();
        context.setUnusedWords(unused);
        context.getBuilder().unusedWords(unused);
        next(context);
    }
}
