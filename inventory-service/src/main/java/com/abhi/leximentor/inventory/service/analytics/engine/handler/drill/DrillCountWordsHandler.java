package com.abhi.leximentor.inventory.service.analytics.engine.handler.drill;

import com.abhi.leximentor.inventory.service.analytics.engine.BaseAnalyticsHandler;
import com.abhi.leximentor.inventory.service.analytics.engine.context.DrillAnalyticsContext;
import com.abhi.leximentor.inventory.util.CollectionUtil;

public class DrillCountWordsHandler extends BaseAnalyticsHandler<DrillAnalyticsContext> {
    @Override
    public void handle(DrillAnalyticsContext context) {
        int count = CollectionUtil.isEmpty(context.getDrillMetadata().getDrillSetList())
                ? 0
                : context.getDrillMetadata().getDrillSetList().size();
        context.getBuilder().countOfWordsLearned(count);
        next(context);
    }
}
