package com.abhi.leximentor.fitmate.constants;

import org.apache.commons.lang3.StringUtils;

public enum Unit {

    TIME("time"),
    REPS("reps"),
    WEIGHT("weight");

    private final String value;

    Unit(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Unit from(String input) {
        for (Unit unit : values()) {
            if (StringUtils.equalsIgnoreCase(unit.value, input)) {
                return unit;
            }
        }
        throw new IllegalArgumentException("Invalid unit: " + input);
    }
}
