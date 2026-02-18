package com.abhi.leximentor.leximentor.service.analytics.engine.handler.drill;

import com.abhi.leximentor.leximentor.dto.inv.WordDTO;
import com.abhi.leximentor.leximentor.entities.drill.Challenge;
import com.abhi.leximentor.leximentor.entities.drill.ChallengeScores;
import com.abhi.leximentor.leximentor.entities.drill.DrillSet;
import com.abhi.leximentor.leximentor.entities.inv.WordMetadata;
import com.abhi.leximentor.leximentor.mapper.InventoryDomainMapper;
import com.abhi.leximentor.leximentor.service.analytics.engine.BaseAnalyticsHandler;
import com.abhi.leximentor.leximentor.service.analytics.engine.context.DrillAnalyticsContext;
import com.abhi.leximentor.leximentor.util.CollectionUtil;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DrillTopChallengingWordsHandler extends BaseAnalyticsHandler<DrillAnalyticsContext> {
    private final InventoryDomainMapper inventoryDomainMapper;

    public DrillTopChallengingWordsHandler(InventoryDomainMapper inventoryDomainMapper) {
        this.inventoryDomainMapper = inventoryDomainMapper;
    }

    @Override
    public void handle(DrillAnalyticsContext context) {
        int topN = context.getTopN();
        if (topN <= 0 || CollectionUtil.isEmpty(context.getDrillMetadata().getChallenges())) {
            context.getBuilder().topChallengingWordsInTheDrill(List.of());
            next(context);
            return;
        }

        Map<WordMetadata, Integer> wrongCounts = new HashMap<>();
        for (Challenge challenge : context.getDrillMetadata().getChallenges()) {
            if (CollectionUtil.isEmpty(challenge.getChallengeScoresList())) {
                continue;
            }
            for (ChallengeScores score : challenge.getChallengeScoresList()) {
                if (score == null || score.isCorrect()) {
                    continue;
                }
                DrillSet drillSet = score.getDrillSetId();
                if (drillSet == null || drillSet.getWordId() == null) {
                    continue;
                }
                WordMetadata word = drillSet.getWordId();
                wrongCounts.merge(word, 1, Integer::sum);
            }
        }

        List<WordDTO> topWords = wrongCounts.entrySet().stream()
                .sorted(Map.Entry.<WordMetadata, Integer>comparingByValue(Comparator.reverseOrder()))
                .limit(topN)
                .map(entry -> inventoryDomainMapper.toDto(entry.getKey()))
                .collect(Collectors.toList());

        context.getBuilder().topChallengingWordsInTheDrill(topWords);
        next(context);
    }
}
