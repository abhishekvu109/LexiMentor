package com.abhi.leximentor.leximentor.service.analytics.engine.handler.word;

import com.abhi.leximentor.leximentor.repository.inv.WordMetadataRepository;
import com.abhi.leximentor.leximentor.service.analytics.engine.BaseAnalyticsHandler;
import com.abhi.leximentor.leximentor.service.analytics.engine.context.WordDistributionContext;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WordCategoryDistributionHandler extends BaseAnalyticsHandler<WordDistributionContext> {
    private final WordMetadataRepository wordMetadataRepository;

    public WordCategoryDistributionHandler(WordMetadataRepository wordMetadataRepository) {
        this.wordMetadataRepository = wordMetadataRepository;
    }

    @Override
    public void handle(WordDistributionContext context) {
        List<Object[]> rows = wordMetadataRepository.findCategoryDistribution();
        Map<String, Long> distribution = new LinkedHashMap<>();
        for (Object[] row : rows) {
            String category = row[0] == null ? "UNCATEGORIZED" : String.valueOf(row[0]);
            long count = row[1] == null ? 0L : ((Number) row[1]).longValue();
            distribution.put(category, count);
        }
        context.setCategoryDistribution(distribution);
        context.getBuilder().categoryDistribution(distribution);
        next(context);
    }
}
