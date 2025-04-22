package com.abhi.leximentor.inventory.service.analytics.impl;

import com.abhi.leximentor.inventory.constants.DrillTypes;
import com.abhi.leximentor.inventory.dto.analytics.DrillAnalyticsDTO;
import com.abhi.leximentor.inventory.dto.inv.WordDTO;
import com.abhi.leximentor.inventory.entities.drill.DrillChallenge;
import com.abhi.leximentor.inventory.exceptions.entities.ServerException;
import com.abhi.leximentor.inventory.repository.drill.DrillMetadataRepository;
import com.abhi.leximentor.inventory.service.analytics.DrillAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DrillAnalyticsServiceImpl implements DrillAnalyticsService {
    private final DrillMetadataRepository drillMetadataRepository;

    @Override
    public int getCountOfWordsLearned(long drillRefId) {
        return drillMetadataRepository.findByRefId(drillRefId).getDrillSetList().size();
    }

    @Override
    public double getDrillSuccessInPercentage(long drillRefId) {
        return drillMetadataRepository.findByRefId(drillRefId).getDrillChallenges().stream().mapToDouble(DrillChallenge::getDrillScore).sum();
    }

    @Override
    public List<WordDTO> getTopNChallengingWordsInTheDrill(int N) {
        return null;
    }

    @Override
    public double getAvgDrillScore(long drillRefId) {
        OptionalDouble optionalDouble = drillMetadataRepository.findByRefId(drillRefId).getDrillChallenges().stream().mapToDouble(DrillChallenge::getDrillScore).average();
        if (optionalDouble.isPresent()) return optionalDouble.getAsDouble();
        else throw new ServerException().new InternalError("Unable to compute.");
    }

    @Override
    public double getAvgDrillScoreByType(long drillRefId, DrillTypes drillTypes) {
        OptionalDouble optionalDouble = drillMetadataRepository.findByRefId(drillRefId).getDrillChallenges().stream().filter(drillChallenge -> StringUtils.equalsIgnoreCase(drillChallenge.getDrillType(), drillTypes.name())).mapToDouble(DrillChallenge::getDrillScore).average();
        if (optionalDouble.isPresent()) return optionalDouble.getAsDouble();
        else throw new ServerException().new InternalError("Unable to compute.");
    }

    @Override
    public Map<DrillTypes, Integer> getCountOfDrillTypesPerDrill(long drillRefId) {
        return drillMetadataRepository.findByRefId(drillRefId).getDrillChallenges().stream().collect(Collectors.groupingBy(challenge -> DrillTypes.getType(challenge.getDrillType()), Collectors.summingInt(challenge -> 1) // Summing integers instead of using `counting()` and conversion
        ));
    }

    @Override
    public Map<DrillTypes, Double> getAvgDrillScoreOfAllDrills(long drillRefId) {
        return drillMetadataRepository.findByRefId(drillRefId).getDrillChallenges().stream().collect(Collectors.groupingBy(challenge -> DrillTypes.getType(challenge.getDrillType()), Collectors.averagingDouble(DrillChallenge::getDrillScore) // Summing integers instead of using `counting()` and conversion
        ));
    }

    @Override
    public int getCountOfChallengesInADrill(long drillRefId) {
        return drillMetadataRepository.findByRefId(drillRefId).getDrillChallenges().size();
    }

    @Override
    public DrillAnalyticsDTO getDrillAnalyticsData(long drillRefId) {
        return DrillAnalyticsDTO.builder()
                .countOfWordsLearned(this.getCountOfWordsLearned(drillRefId))
                .avgDrillScore(this.getAvgDrillScore(drillRefId))
                .drillSuccessInPercentage(this.getDrillSuccessInPercentage(drillRefId))
                .topChallengingWordsInTheDrill(this.getTopNChallengingWordsInTheDrill(10))
                .countOfChallenges(this.getCountOfChallengesInADrill(drillRefId))
                .build();
    }
}
