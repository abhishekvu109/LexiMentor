package com.abhi.leximentor.inventory.service.analytics.engine.handler.word;

import com.abhi.leximentor.inventory.service.analytics.engine.BaseAnalyticsHandler;
import com.abhi.leximentor.inventory.service.analytics.engine.context.WordDistributionContext;

public class WordUnusedPercentageHandler extends BaseAnalyticsHandler<WordDistributionContext> {
    @Override
    public void handle(WordDistributionContext context) {
        if (context.getTotalWords() <= 0) {
            context.getBuilder().unusedPercentage(0.0);
            next(context);
            return;
        }

        double percentage = (context.getUnusedWords() * 100.0) / context.getTotalWords();
        context.getBuilder().unusedPercentage(Math.round(percentage * 100.0) / 100.0);
        next(context);
    }
}
