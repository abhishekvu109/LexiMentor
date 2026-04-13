package com.abhi.leximentor.leximentor.service.drill.impl.strategy;

import com.abhi.leximentor.leximentor.constants.ChallengeType;
import com.abhi.leximentor.leximentor.exceptions.entities.ServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class DrillEvaluationStrategyRegistry {
    private final Map<ChallengeType, DrillEvaluationStrategy> strategies = new EnumMap<>(ChallengeType.class);

    @Autowired
    public DrillEvaluationStrategyRegistry(List<DrillEvaluationStrategy> strategies) {
        for (DrillEvaluationStrategy strategy : strategies) {
            this.strategies.put(strategy.getType(), strategy);
        }
    }

    public DrillEvaluationStrategy getStrategy(ChallengeType type) {
        DrillEvaluationStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new ServerException().new InternalError("No evaluation strategy registered for type: " + type);
        }
        return strategy;
    }

    public DrillEvaluationStrategy getStrategyOrDefault(ChallengeType type, ChallengeType defaultType) {
        DrillEvaluationStrategy strategy = strategies.get(type);
        if (strategy != null) {
            return strategy;
        }
        DrillEvaluationStrategy fallback = strategies.get(defaultType);
        if (fallback == null) {
            throw new ServerException().new InternalError("No evaluation strategy registered for fallback type: " + defaultType);
        }
        return fallback;
    }
}
