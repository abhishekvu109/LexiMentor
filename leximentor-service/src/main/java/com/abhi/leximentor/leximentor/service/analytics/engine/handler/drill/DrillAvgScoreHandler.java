package com.abhi.leximentor.leximentor.service.analytics.engine.handler.drill;

import com.abhi.leximentor.leximentor.entities.drill.Challenge;
import com.abhi.leximentor.leximentor.service.analytics.engine.BaseAnalyticsHandler;
import com.abhi.leximentor.leximentor.service.analytics.engine.context.DrillAnalyticsContext;
import com.abhi.leximentor.leximentor.util.CollectionUtil;

public class DrillAvgScoreHandler extends BaseAnalyticsHandler<DrillAnalyticsContext> {
    @Override
    public void handle(DrillAnalyticsContext context) {
        if (CollectionUtil.isEmpty(context.getDrillMetadata().getChallenges())) {
            context.getBuilder().avgDrillScore(0.0);
            next(context);
            return;
        }

        double average = context.getDrillMetadata().getChallenges().stream()
                .mapToDouble(Challenge::getScore)
                .average()
                .orElse(0.0);
        context.getBuilder().avgDrillScore(Math.round(average * 100.0) / 100.0);
        next(context);
    }
}
