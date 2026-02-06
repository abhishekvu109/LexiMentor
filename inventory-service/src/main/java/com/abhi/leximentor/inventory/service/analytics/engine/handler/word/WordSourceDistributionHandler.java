package com.abhi.leximentor.inventory.service.analytics.engine.handler.word;

import com.abhi.leximentor.inventory.repository.inv.WordMetadataRepository;
import com.abhi.leximentor.inventory.service.analytics.engine.BaseAnalyticsHandler;
import com.abhi.leximentor.inventory.service.analytics.engine.context.WordDistributionContext;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WordSourceDistributionHandler extends BaseAnalyticsHandler<WordDistributionContext> {
    private final WordMetadataRepository wordMetadataRepository;

    public WordSourceDistributionHandler(WordMetadataRepository wordMetadataRepository) {
        this.wordMetadataRepository = wordMetadataRepository;
    }

    @Override
    public void handle(WordDistributionContext context) {
        List<Object[]> rows = wordMetadataRepository.findSourceDistribution();
        Map<String, Long> distribution = new LinkedHashMap<>();
        for (Object[] row : rows) {
            String source = row[0] == null ? "UNKNOWN" : String.valueOf(row[0]);
            long count = row[1] == null ? 0L : ((Number) row[1]).longValue();
            distribution.put(source, count);
        }
        context.setSourceDistribution(distribution);
        context.getBuilder().sourceDistribution(distribution);
        next(context);
    }
}
