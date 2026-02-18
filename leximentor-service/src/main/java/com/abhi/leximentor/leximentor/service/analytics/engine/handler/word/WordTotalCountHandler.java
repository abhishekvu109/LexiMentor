package com.abhi.leximentor.leximentor.service.analytics.engine.handler.word;

import com.abhi.leximentor.leximentor.repository.inv.WordMetadataRepository;
import com.abhi.leximentor.leximentor.service.analytics.engine.BaseAnalyticsHandler;
import com.abhi.leximentor.leximentor.service.analytics.engine.context.WordAnalyticsContext;

public class WordTotalCountHandler extends BaseAnalyticsHandler<WordAnalyticsContext> {
    private final WordMetadataRepository wordMetadataRepository;

    public WordTotalCountHandler(WordMetadataRepository wordMetadataRepository) {
        this.wordMetadataRepository = wordMetadataRepository;
    }

    @Override
    public void handle(WordAnalyticsContext context) {
        context.getBuilder().totalWords(wordMetadataRepository.count());
        next(context);
    }
}
