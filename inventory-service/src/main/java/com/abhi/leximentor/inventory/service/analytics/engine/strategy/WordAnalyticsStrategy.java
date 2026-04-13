package com.abhi.leximentor.inventory.service.analytics.engine.strategy;

import com.abhi.leximentor.inventory.dto.analytics.WordAnalyticsDTO;
import com.abhi.leximentor.inventory.repository.inv.WordMetadataRepository;
import com.abhi.leximentor.inventory.service.analytics.engine.*;
import com.abhi.leximentor.inventory.service.analytics.engine.context.WordAnalyticsContext;
import com.abhi.leximentor.inventory.service.analytics.engine.handler.word.WordPosDistributionHandler;
import com.abhi.leximentor.inventory.service.analytics.engine.handler.word.WordTotalCountHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WordAnalyticsStrategy implements AnalyticsStrategy<WordAnalyticsDTO> {
    private final WordMetadataRepository wordMetadataRepository;

    @Override
    public AnalyticsType getType() {
        return AnalyticsType.WORD_METADATA;
    }

    @Override
    public WordAnalyticsDTO execute(AnalyticsRequest request) {
        WordAnalyticsContext context = WordAnalyticsContext.builder()
                .builder(WordAnalyticsDTO.builder())
                .build();
        buildPipeline().execute(context);
        return context.getBuilder().build();
    }

    private AnalyticsPipeline<WordAnalyticsContext> buildPipeline() {
        WordTotalCountHandler totalCountHandler = new WordTotalCountHandler(wordMetadataRepository);
        WordPosDistributionHandler posDistributionHandler = new WordPosDistributionHandler(wordMetadataRepository);

        totalCountHandler.setNext(posDistributionHandler);
        return new AnalyticsPipeline<>(totalCountHandler);
    }
}
