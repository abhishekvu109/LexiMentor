package com.abhi.leximentor.inventory.util;

import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class CollectionUtil {
    public static <T> boolean isEmpty(Collection<T> list) {
        return (list == null || list.isEmpty());
    }

    public static <T> boolean isNotEmpty(Collection<T> list) {
        return !isEmpty(list);
    }
}
