package com.abhi.leximentor.inventory.service.analytics;

import com.abhi.leximentor.inventory.constants.DrillTypes;
import com.abhi.leximentor.inventory.dto.analytics.DrillAnalyticsDTO;
import com.abhi.leximentor.inventory.dto.inv.WordDTO;

import java.util.List;
import java.util.Map;

public interface DrillAnalyticsService {
    public int getCountOfWordsLearned(long drillRefId);

    public double getDrillSuccessInPercentage(long drillRefId);

    public List<WordDTO> getTopNChallengingWordsInTheDrill(int N);

    public double getAvgDrillScore(long drillRefId);

    public double getAvgDrillScoreByType(long drillRefId, DrillTypes drillTypes);

    public Map<DrillTypes, Integer> getCountOfDrillTypesPerDrill(long drillRefId);

    public Map<DrillTypes, Double> getAvgDrillScoreOfAllDrills(long drillRefId);

    public int getCountOfChallengesInADrill(long drillRefId);

    public DrillAnalyticsDTO getDrillAnalyticsData(long drillRefId);

}
