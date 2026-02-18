package com.abhi.leximentor.leximentor.service.analytics.engine.handler.drill;

import com.abhi.leximentor.leximentor.service.analytics.engine.BaseAnalyticsHandler;
import com.abhi.leximentor.leximentor.service.analytics.engine.context.DrillAnalyticsContext;
import com.abhi.leximentor.leximentor.util.CollectionUtil;

public class DrillCountChallengesHandler extends BaseAnalyticsHandler<DrillAnalyticsContext> {
    @Override
    public void handle(DrillAnalyticsContext context) {
        int count = CollectionUtil.isEmpty(context.getDrillMetadata().getChallenges())
                ? 0
                : context.getDrillMetadata().getChallenges().size();
        context.getBuilder().countOfChallenges(count);
        next(context);
    }
}
