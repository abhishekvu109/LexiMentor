package com.abhi.leximentor.leximentor.service.analytics.engine.handler.drill;

import com.abhi.leximentor.leximentor.entities.drill.DrillMetadata;
import com.abhi.leximentor.leximentor.exceptions.entities.ServerException;
import com.abhi.leximentor.leximentor.repository.drill.DrillMetadataRepository;
import com.abhi.leximentor.leximentor.service.analytics.engine.BaseAnalyticsHandler;
import com.abhi.leximentor.leximentor.service.analytics.engine.context.DrillAnalyticsContext;

public class DrillMetadataLoadHandler extends BaseAnalyticsHandler<DrillAnalyticsContext> {
    private final DrillMetadataRepository drillMetadataRepository;

    public DrillMetadataLoadHandler(DrillMetadataRepository drillMetadataRepository) {
        this.drillMetadataRepository = drillMetadataRepository;
    }

    @Override
    public void handle(DrillAnalyticsContext context) {
        DrillMetadata drillMetadata = drillMetadataRepository.findByRefId(context.getDrillRefId());
        if (drillMetadata == null) {
            throw new ServerException().new EntityObjectNotFound("Drill not found for refId: " + context.getDrillRefId());
        }
        context.setDrillMetadata(drillMetadata);
        next(context);
    }
}
