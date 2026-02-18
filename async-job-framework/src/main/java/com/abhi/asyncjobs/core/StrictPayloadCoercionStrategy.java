package com.abhi.asyncjobs.core;

public enum StrictPayloadCoercionStrategy implements PayloadCoercionStrategy {
    INSTANCE;

    @Override
    public Object coerce(Object payload, Class<?> targetType) {
        return payload;
    }
}
