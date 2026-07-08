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

    public <T> java.util.List<T> executeList(AnalyticsType type, AnalyticsRequest request, Class<T> elementType) {
        AnalyticsStrategy<?> strategy = strategies.get(type);
        if (strategy == null) {
            throw new ServerException().new InternalError("No analytics strategy registered for type: " + type);
        }
        Object result = strategy.execute(request);
        if (!(result instanceof java.util.List<?> list)) {
            throw new ServerException().new InternalError("Expected list response for type: " + type);
        }
        java.util.List<T> typed = new java.util.ArrayList<>(list.size());
        for (Object item : list) {
            typed.add(elementType.cast(item));
        }
        return typed;
    }

    public <K, V> java.util.Map<K, V> executeMap(AnalyticsType type, AnalyticsRequest request, Class<K> keyType, Class<V> valueType) {
        AnalyticsStrategy<?> strategy = strategies.get(type);
        if (strategy == null) {
            throw new ServerException().new InternalError("No analytics strategy registered for type: " + type);
        }
        Object result = strategy.execute(request);
        if (!(result instanceof java.util.Map<?, ?> map)) {
            throw new ServerException().new InternalError("Expected map response for type: " + type);
        }
        java.util.Map<K, V> typed = new java.util.LinkedHashMap<>();
        for (java.util.Map.Entry<?, ?> entry : map.entrySet()) {
            typed.put(keyType.cast(entry.getKey()), valueType.cast(entry.getValue()));
        }
        return typed;
    }

    public <T> org.springframework.data.domain.Page<T> executePage(AnalyticsType type, AnalyticsRequest request, Class<T> elementType) {
        AnalyticsStrategy<?> strategy = strategies.get(type);
        if (strategy == null) {
            throw new ServerException().new InternalError("No analytics strategy registered for type: " + type);
        }
        Object result = strategy.execute(request);
        if (!(result instanceof org.springframework.data.domain.Page<?> page)) {
            throw new ServerException().new InternalError("Expected page response for type: " + type);
        }
        for (Object item : page.getContent()) {
            elementType.cast(item);
        }
        @SuppressWarnings("unchecked")
        org.springframework.data.domain.Page<T> typed = (org.springframework.data.domain.Page<T>) page;
        return typed;
    }
}
