package com.abhi.leximentor.inventory.service.analytics.engine.handler.drill;

import com.abhi.leximentor.inventory.dto.inv.WordDTO;
import com.abhi.leximentor.inventory.entities.drill.DrillChallenge;
import com.abhi.leximentor.inventory.entities.drill.DrillChallengeScores;
import com.abhi.leximentor.inventory.entities.drill.DrillSet;
import com.abhi.leximentor.inventory.entities.inv.WordMetadata;
import com.abhi.leximentor.inventory.service.analytics.engine.BaseAnalyticsHandler;
import com.abhi.leximentor.inventory.service.analytics.engine.context.DrillAnalyticsContext;
import com.abhi.leximentor.inventory.service.inv.impl.InventoryServiceUtil;
import com.abhi.leximentor.inventory.util.CollectionUtil;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DrillTopChallengingWordsHandler extends BaseAnalyticsHandler<DrillAnalyticsContext> {
    @Override
    public void handle(DrillAnalyticsContext context) {
        int topN = context.getTopN();
        if (topN <= 0 || CollectionUtil.isEmpty(context.getDrillMetadata().getDrillChallenges())) {
            context.getBuilder().topChallengingWordsInTheDrill(List.of());
            next(context);
            return;
        }

        Map<WordMetadata, Integer> wrongCounts = new HashMap<>();
        for (DrillChallenge challenge : context.getDrillMetadata().getDrillChallenges()) {
            if (CollectionUtil.isEmpty(challenge.getDrillChallengeScoresList())) {
                continue;
            }
            for (DrillChallengeScores score : challenge.getDrillChallengeScoresList()) {
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
                .map(entry -> InventoryServiceUtil.WordMetadataUtil.buildDTO(entry.getKey()))
                .collect(Collectors.toList());

        context.getBuilder().topChallengingWordsInTheDrill(topWords);
        next(context);
    }
}
