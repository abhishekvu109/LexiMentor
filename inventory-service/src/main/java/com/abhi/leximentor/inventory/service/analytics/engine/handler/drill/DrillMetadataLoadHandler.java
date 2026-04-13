package com.abhi.leximentor.inventory.service.analytics.engine.handler.drill;

import com.abhi.leximentor.inventory.entities.drill.DrillMetadata;
import com.abhi.leximentor.inventory.exceptions.entities.ServerException;
import com.abhi.leximentor.inventory.repository.drill.DrillMetadataRepository;
import com.abhi.leximentor.inventory.service.analytics.engine.BaseAnalyticsHandler;
import com.abhi.leximentor.inventory.service.analytics.engine.context.DrillAnalyticsContext;

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
