package com.abhi.saarthi.cashflow.constants;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;


@AllArgsConstructor
public enum Severity {
    LOW("LOW"), MEDIUM("MEDIUM"), HIGH("HIGH");
    private final String value;

    public static Severity from(String value) {
        if (StringUtils.equalsIgnoreCase(value, LOW.value)) {
            return LOW;
        } else if (StringUtils.equalsIgnoreCase(value, MEDIUM.value)) {
            return MEDIUM;
        } else if (StringUtils.equalsIgnoreCase(value, HIGH.value)) {
            return HIGH;
        } else {
            return LOW;
        }
    }
}
