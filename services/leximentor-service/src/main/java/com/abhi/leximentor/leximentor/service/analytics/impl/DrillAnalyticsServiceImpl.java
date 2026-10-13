package com.abhi.leximentor.leximentor.service.analytics.impl;

import com.abhi.leximentor.leximentor.constants.ChallengeType;
import com.abhi.leximentor.leximentor.dto.analytics.DrillAnalyticsDTO;
import com.abhi.leximentor.leximentor.dto.inv.WordDTO;
import com.abhi.leximentor.leximentor.entities.drill.Drill;
import com.abhi.leximentor.leximentor.entities.drill.Challenge;
import com.abhi.leximentor.leximentor.exceptions.entities.ServerException;
import com.abhi.leximentor.leximentor.repository.drill.DrillRepository;
import com.abhi.leximentor.leximentor.service.analytics.DrillAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final DrillRepository drillRepository;

    private Drill getDrillByKey(String drillKey) {
        return drillRepository.findByKey(drillKey)
                .orElseThrow(() -> new ServerException().new EntityObjectNotFound("Drill not found for key: " + drillKey));
    }

    @Override
    public int getCountOfWordsLearned(String drillKey) {
        log.info("Calculating count of words learned. drillKey={}", drillKey);
        return getDrillByKey(drillKey).getDrillSetList().size();
    }

    @Override
    public double getDrillSuccessInPercentage(String drillKey) {
        log.info("Calculating drill success percentage. drillKey={}", drillKey);
        var challenges = getDrillByKey(drillKey).getChallenges();

        if (challenges == null || challenges.isEmpty()) {
            return 0.0;
        }

        double average = challenges.stream()
                .mapToDouble(Challenge::getScore)
                .average()
                .orElse(0.0);

        return Math.round(average * 100.0) / 100.0;
    }

    @Override
    public List<WordDTO> getTopNChallengingWordsInTheDrill(int N) {
        return null;
    }

    @Override
    public double getAvgDrillScore(String drillKey) {
        log.info("Calculating avg drill score. drillKey={}", drillKey);
        OptionalDouble optionalDouble = getDrillByKey(drillKey).getChallenges().stream().mapToDouble(Challenge::getScore).average();
        if (optionalDouble.isPresent()) {
            return Math.round(optionalDouble.getAsDouble() * 100.0) / 100.0;
        } else {
            return 0.0;
        }
    }

    @Override
    public double getAvgDrillScoreByType(String drillKey, ChallengeType challengeType) {
        log.info("Calculating avg drill score by type. drillKey={}, drillType={}", drillKey, challengeType);
        OptionalDouble optionalDouble = getDrillByKey(drillKey).getChallenges().stream()
                .filter(drillChallenge -> drillChallenge.getChallengeType() == challengeType)
                .mapToDouble(Challenge::getScore)
                .average();
        if (optionalDouble.isPresent()) return optionalDouble.getAsDouble();
        else throw new ServerException().new InternalError("Unable to compute.");
    }

    @Override
    public Map<ChallengeType, Integer> getCountOfDrillTypesPerDrill(String drillKey) {
        log.info("Calculating drill type counts. drillKey={}", drillKey);
        return getDrillByKey(drillKey).getChallenges().stream().collect(Collectors.groupingBy(Challenge::getChallengeType, Collectors.summingInt(challenge -> 1) // Summing integers instead of using `counting()` and conversion
        ));
    }

    @Override
    public Map<ChallengeType, Double> getAvgDrillScoreOfAllDrills(String drillKey) {
        log.info("Calculating avg drill score across types. drillKey={}", drillKey);
        return getDrillByKey(drillKey).getChallenges().stream().collect(Collectors.groupingBy(Challenge::getChallengeType, Collectors.averagingDouble(Challenge::getScore) // Summing integers instead of using `counting()` and conversion
        ));
    }

    @Override
    public int getCountOfChallengesInADrill(String drillKey) {
        log.info("Calculating count of challenges. drillKey={}", drillKey);
        return getDrillByKey(drillKey).getChallenges().size();
    }

    @Override
    public DrillAnalyticsDTO getDrillAnalyticsData(String drillKey) {
        log.info("Building drill analytics DTO. drillKey={}", drillKey);
        return DrillAnalyticsDTO.builder()
                .countOfWordsLearned(this.getCountOfWordsLearned(drillKey))
                .avgDrillScore(this.getAvgDrillScore(drillKey))
                .drillSuccessInPercentage(this.getDrillSuccessInPercentage(drillKey))
                .topChallengingWordsInTheDrill(this.getTopNChallengingWordsInTheDrill(10))
                .countOfChallenges(this.getCountOfChallengesInADrill(drillKey))
                .build();
    }
}
