package com.abhi.asyncjobs.core;

public interface PayloadCoercionStrategy {
    Object coerce(Object payload, Class<?> targetType);
}
