package com.abhi.leximentor.leximentor.service.analytics.engine.handler.word;

import com.abhi.leximentor.leximentor.repository.inv.WordMetadataRepository;
import com.abhi.leximentor.leximentor.service.analytics.engine.BaseAnalyticsHandler;
import com.abhi.leximentor.leximentor.service.analytics.engine.context.WordDistributionContext;

public class WordDistributionTotalCountHandler extends BaseAnalyticsHandler<WordDistributionContext> {
    private final WordMetadataRepository wordMetadataRepository;

    public WordDistributionTotalCountHandler(WordMetadataRepository wordMetadataRepository) {
        this.wordMetadataRepository = wordMetadataRepository;
    }

    @Override
    public void handle(WordDistributionContext context) {
        long total = wordMetadataRepository.count();
        context.setTotalWords(total);
        context.getBuilder().totalWords(total);
        next(context);
    }
}
