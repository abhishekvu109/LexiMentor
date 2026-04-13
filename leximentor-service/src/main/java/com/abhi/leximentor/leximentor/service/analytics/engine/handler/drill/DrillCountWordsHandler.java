package com.abhi.leximentor.leximentor.service.analytics.engine.handler.drill;

import com.abhi.leximentor.leximentor.service.analytics.engine.BaseAnalyticsHandler;
import com.abhi.leximentor.leximentor.service.analytics.engine.context.DrillAnalyticsContext;
import com.abhi.leximentor.leximentor.util.CollectionUtil;

public class DrillCountWordsHandler extends BaseAnalyticsHandler<DrillAnalyticsContext> {
    @Override
    public void handle(DrillAnalyticsContext context) {
        int count = CollectionUtil.isEmpty(context.getDrill().getDrillSetList())
                ? 0
                : context.getDrill().getDrillSetList().size();
        context.getBuilder().countOfWordsLearned(count);
        next(context);
    }
}
