package com.abhi.leximentor.leximentor.service.analytics.engine.strategy;

import com.abhi.leximentor.leximentor.dto.analytics.DrillAnalyticsDTO;
import com.abhi.leximentor.leximentor.mapper.InventoryDomainMapper;
import com.abhi.leximentor.leximentor.repository.drill.DrillRepository;
import com.abhi.leximentor.leximentor.service.analytics.engine.*;
import com.abhi.leximentor.leximentor.service.analytics.engine.context.DrillAnalyticsContext;
import com.abhi.leximentor.leximentor.service.analytics.engine.handler.drill.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DrillAnalyticsStrategy implements AnalyticsStrategy<DrillAnalyticsDTO> {
    private static final int DEFAULT_TOP_N = 10;

    private final DrillRepository drillRepository;
    private final InventoryDomainMapper inventoryDomainMapper;

    @Override
    public AnalyticsType getType() {
        return AnalyticsType.DRILL;
    }

    @Override
    public DrillAnalyticsDTO execute(AnalyticsRequest request) {
        int topN = request.getTopN() == null ? DEFAULT_TOP_N : request.getTopN();
        DrillAnalyticsContext context = DrillAnalyticsContext.builder()
                .drillRefId(request.getDrillRefId())
                .topN(topN)
                .builder(DrillAnalyticsDTO.builder())
                .build();

        buildPipeline().execute(context);
        return context.getBuilder().build();
    }

    private AnalyticsPipeline<DrillAnalyticsContext> buildPipeline() {
        DrillMetadataLoadHandler metadataLoadHandler = new DrillMetadataLoadHandler(drillRepository);
        DrillCountWordsHandler countWordsHandler = new DrillCountWordsHandler();
        DrillAvgScoreHandler avgScoreHandler = new DrillAvgScoreHandler();
        DrillSuccessRateHandler successRateHandler = new DrillSuccessRateHandler();
        DrillCountChallengesHandler countChallengesHandler = new DrillCountChallengesHandler();
        DrillTopChallengingWordsHandler topWordsHandler = new DrillTopChallengingWordsHandler(inventoryDomainMapper);

        metadataLoadHandler.setNext(countWordsHandler);
        countWordsHandler.setNext(avgScoreHandler);
        avgScoreHandler.setNext(successRateHandler);
        successRateHandler.setNext(countChallengesHandler);
        countChallengesHandler.setNext(topWordsHandler);

        return new AnalyticsPipeline<>(metadataLoadHandler);
    }
}
