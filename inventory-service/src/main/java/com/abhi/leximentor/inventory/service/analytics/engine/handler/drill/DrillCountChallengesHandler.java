package com.abhi.leximentor.inventory.service.analytics.engine.handler.drill;

import com.abhi.leximentor.inventory.service.analytics.engine.BaseAnalyticsHandler;
import com.abhi.leximentor.inventory.service.analytics.engine.context.DrillAnalyticsContext;
import com.abhi.leximentor.inventory.util.CollectionUtil;

public class DrillCountChallengesHandler extends BaseAnalyticsHandler<DrillAnalyticsContext> {
    @Override
    public void handle(DrillAnalyticsContext context) {
        int count = CollectionUtil.isEmpty(context.getDrillMetadata().getDrillChallenges())
                ? 0
                : context.getDrillMetadata().getDrillChallenges().size();
        context.getBuilder().countOfChallenges(count);
        next(context);
    }
}
