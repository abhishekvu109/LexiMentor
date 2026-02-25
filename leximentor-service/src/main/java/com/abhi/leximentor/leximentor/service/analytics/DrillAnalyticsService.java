package com.abhi.leximentor.leximentor.service.analytics;

import com.abhi.leximentor.leximentor.constants.ChallengeType;
import com.abhi.leximentor.leximentor.dto.analytics.DrillAnalyticsDTO;
import com.abhi.leximentor.leximentor.dto.inv.WordDTO;

import java.util.List;
import java.util.Map;

public interface DrillAnalyticsService {
    public int getCountOfWordsLearned(String drillKey);

    public double getDrillSuccessInPercentage(String drillKey);

    public List<WordDTO> getTopNChallengingWordsInTheDrill(int N);

    public double getAvgDrillScore(String drillKey);

    public double getAvgDrillScoreByType(String drillKey, ChallengeType challengeType);

    public Map<ChallengeType, Integer> getCountOfDrillTypesPerDrill(String drillKey);

    public Map<ChallengeType, Double> getAvgDrillScoreOfAllDrills(String drillKey);

    public int getCountOfChallengesInADrill(String drillKey);

    public DrillAnalyticsDTO getDrillAnalyticsData(String drillKey);

}
