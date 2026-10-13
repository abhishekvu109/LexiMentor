package com.abhi.leximentor.leximentor.service.analytics.engine.handler.drill;

import com.abhi.leximentor.leximentor.entities.drill.Drill;
import com.abhi.leximentor.leximentor.exceptions.entities.ServerException;
import com.abhi.leximentor.leximentor.repository.drill.DrillRepository;
import com.abhi.leximentor.leximentor.service.analytics.engine.BaseAnalyticsHandler;
import com.abhi.leximentor.leximentor.service.analytics.engine.context.DrillAnalyticsContext;

public class DrillMetadataLoadHandler extends BaseAnalyticsHandler<DrillAnalyticsContext> {
    private final DrillRepository drillRepository;

    public DrillMetadataLoadHandler(DrillRepository drillRepository) {
        this.drillRepository = drillRepository;
    }

    @Override
    public void handle(DrillAnalyticsContext context) {
        Drill drill = drillRepository.findByKey(context.getDrillKey()).orElse(null);
        if (drill == null) {
            throw new ServerException().new EntityObjectNotFound("Drill not found for key: " + context.getDrillKey());
        }
        context.setDrill(drill);
        next(context);
    }
}
