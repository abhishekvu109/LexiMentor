package com.abhi.asyncjobs.starter.autoconfigure;

import com.abhi.asyncjobs.core.PayloadCoercionStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Objects;

public final class JacksonPayloadCoercionStrategy implements PayloadCoercionStrategy {
    private final ObjectMapper objectMapper;

    public JacksonPayloadCoercionStrategy(ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
    }

    @Override
    public Object coerce(Object payload, Class<?> targetType) {
        if (payload == null) {
            return null;
        }
        if (targetType.isAssignableFrom(payload.getClass())) {
            return payload;
        }
        return objectMapper.convertValue(payload, targetType);
    }
}
