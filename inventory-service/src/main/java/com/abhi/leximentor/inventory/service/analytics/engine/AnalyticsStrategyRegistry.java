package com.abhi.leximentor.inventory.service.analytics.engine;

import com.abhi.leximentor.inventory.exceptions.entities.ServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class AnalyticsStrategyRegistry {
    private final Map<AnalyticsType, AnalyticsStrategy<?>> strategies = new EnumMap<>(AnalyticsType.class);

    @Autowired
    public AnalyticsStrategyRegistry(List<AnalyticsStrategy<?>> strategies) {
        for (AnalyticsStrategy<?> strategy : strategies) {
            this.strategies.put(strategy.getType(), strategy);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> AnalyticsStrategy<T> getStrategy(AnalyticsType type) {
        AnalyticsStrategy<?> strategy = strategies.get(type);
        if (strategy == null) {
            throw new ServerException().new InternalError("No analytics strategy registered for type: " + type);
        }
        return (AnalyticsStrategy<T>) strategy;
    }

    public <T> T execute(AnalyticsType type, AnalyticsRequest request, Class<T> responseType) {
        AnalyticsStrategy<?> strategy = strategies.get(type);
        if (strategy == null) {
            throw new ServerException().new InternalError("No analytics strategy registered for type: " + type);
        }
        Object result = strategy.execute(request);
        return responseType.cast(result);
    }
}
