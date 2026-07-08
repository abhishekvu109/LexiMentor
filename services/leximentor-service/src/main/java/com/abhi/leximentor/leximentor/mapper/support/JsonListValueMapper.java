package com.abhi.leximentor.leximentor.mapper.support;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class JsonListValueMapper {
    private JsonListValueMapper() {
    }

    public static String toStorage(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "[]";
        }
        return values.stream()
                .map(value -> "\"" + value + "\"")
                .collect(Collectors.joining(",", "[", "]"));
    }

    public static List<String> fromStorage(String value) {
        if (StringUtils.isEmpty(value)) {
            return List.of();
        }
        return Arrays.stream(value.replace("[", "").replace("]", "").split(",")).toList();
    }
}
