package com.abhi.leximentor.inventory.service.analytics.engine.strategy;

import com.abhi.leximentor.inventory.dto.analytics.WordDistributionDTO;
import com.abhi.leximentor.inventory.repository.inv.WordMetadataRepository;
import com.abhi.leximentor.inventory.service.analytics.engine.*;
import com.abhi.leximentor.inventory.service.analytics.engine.context.WordDistributionContext;
import com.abhi.leximentor.inventory.service.analytics.engine.handler.word.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WordDistributionStrategy implements AnalyticsStrategy<WordDistributionDTO> {
    private final WordMetadataRepository wordMetadataRepository;

    @Override
    public AnalyticsType getType() {
        return AnalyticsType.WORD_DISTRIBUTION;
    }

    @Override
    public WordDistributionDTO execute(AnalyticsRequest request) {
        WordDistributionContext context = WordDistributionContext.builder()
                .builder(WordDistributionDTO.builder())
                .build();

        buildPipeline().execute(context);
        return context.getBuilder().build();
    }

    private AnalyticsPipeline<WordDistributionContext> buildPipeline() {
        WordDistributionTotalCountHandler totalCountHandler = new WordDistributionTotalCountHandler(wordMetadataRepository);
        WordUnusedCountHandler unusedCountHandler = new WordUnusedCountHandler(wordMetadataRepository);
        WordUnusedPercentageHandler unusedPercentageHandler = new WordUnusedPercentageHandler();
        WordSourceDistributionHandler sourceDistributionHandler = new WordSourceDistributionHandler(wordMetadataRepository);
        WordCategoryDistributionHandler categoryDistributionHandler = new WordCategoryDistributionHandler(wordMetadataRepository);

        totalCountHandler.setNext(unusedCountHandler);
        unusedCountHandler.setNext(unusedPercentageHandler);
        unusedPercentageHandler.setNext(sourceDistributionHandler);
        sourceDistributionHandler.setNext(categoryDistributionHandler);

        return new AnalyticsPipeline<>(totalCountHandler);
    }
}
