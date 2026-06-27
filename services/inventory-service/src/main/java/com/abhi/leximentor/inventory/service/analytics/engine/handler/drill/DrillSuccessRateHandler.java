package com.abhi.leximentor.inventory.service.analytics.engine.handler.drill;

import com.abhi.leximentor.inventory.entities.drill.DrillChallenge;
import com.abhi.leximentor.inventory.service.analytics.engine.BaseAnalyticsHandler;
import com.abhi.leximentor.inventory.service.analytics.engine.context.DrillAnalyticsContext;
import com.abhi.leximentor.inventory.util.CollectionUtil;

public class DrillSuccessRateHandler extends BaseAnalyticsHandler<DrillAnalyticsContext> {
    @Override
    public void handle(DrillAnalyticsContext context) {
        if (CollectionUtil.isEmpty(context.getDrillMetadata().getDrillChallenges())) {
            context.getBuilder().drillSuccessInPercentage(0.0);
            next(context);
            return;
        }

        double average = context.getDrillMetadata().getDrillChallenges().stream()
                .mapToDouble(DrillChallenge::getDrillScore)
                .average()
                .orElse(0.0);
        context.getBuilder().drillSuccessInPercentage(Math.round(average * 100.0) / 100.0);
        next(context);
    }
}
