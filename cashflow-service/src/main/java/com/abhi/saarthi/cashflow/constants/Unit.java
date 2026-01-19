package com.abhi.saarthi.cashflow.constants;

import org.apache.commons.lang3.StringUtils;

public enum Unit {

    TIME("time"),
    REPS("reps"),
    WEIGHT("weight");

    private final String value;

    Unit(String value) {
        this.value = value;
    }

    public static Unit from(String input) {
        for (Unit unit : values()) {
            if (StringUtils.equalsIgnoreCase(unit.value, input)) {
                return unit;
            }
        }
        throw new IllegalArgumentException("Invalid unit: " + input);
    }

    public String getValue() {
        return value;
    }
}
