package com.abhi.leximentor.leximentor.service.analytics;

import com.abhi.leximentor.leximentor.constants.DrillTypes;
import com.abhi.leximentor.leximentor.dto.analytics.DrillAnalyticsDTO;
import com.abhi.leximentor.leximentor.dto.inv.WordDTO;

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
