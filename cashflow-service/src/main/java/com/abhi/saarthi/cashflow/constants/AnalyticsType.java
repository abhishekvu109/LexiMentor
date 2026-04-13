package com.abhi.saarthi.cashflow.constants;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@AllArgsConstructor
public enum AnalyticsType {
    CATEGORY("CATEGORY"),
    TIME_SERIES("TIME_SERIES"),
    BUDGET("BUDGET"),
    FORECAST("FORECAST"),
    ANOMALY("ANOMALY"),
    PERIOD_COMPARISON("PERIOD_COMPARISON"),
    MEMBER("MEMBER");

    private final String value;

    public static AnalyticsType from(String value) {
        if (StringUtils.equalsIgnoreCase(value, CATEGORY.value)) {
            return CATEGORY;
        } else if (StringUtils.equalsIgnoreCase(value, TIME_SERIES.value)) {
            return TIME_SERIES;
        } else if (StringUtils.equalsIgnoreCase(value, BUDGET.value)) {
            return BUDGET;
        } else if (StringUtils.equalsIgnoreCase(value, FORECAST.value)) {
            return FORECAST;
        } else if (StringUtils.equalsIgnoreCase(value, ANOMALY.value)) {
            return ANOMALY;
        } else if (StringUtils.equalsIgnoreCase(value, PERIOD_COMPARISON.value)) {
            return PERIOD_COMPARISON;
        } else if (StringUtils.equalsIgnoreCase(value, MEMBER.value)) {
            return MEMBER;
        } else {
            return CATEGORY;
        }
    }

}
