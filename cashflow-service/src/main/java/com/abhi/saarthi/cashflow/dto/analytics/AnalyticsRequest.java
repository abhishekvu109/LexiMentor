package com.abhi.saarthi.cashflow.dto.analytics;

import com.abhi.saarthi.cashflow.constants.AnalyticsType;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Builder
@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsRequest {

    private String householdRefId;
    private LocalDate from;
    private LocalDate to;
    private LocalDate compareFrom;
    private LocalDate compareTo;
    private Set<AnalyticsType> analyticsTypes;

    public static Set<AnalyticsType> defaultAnalyticsTypes() {
        return Set.of(
                AnalyticsType.ANOMALY,
                AnalyticsType.CATEGORY,
                AnalyticsType.MEMBER,
                AnalyticsType.TIME_SERIES,
                AnalyticsType.BUDGET,
                AnalyticsType.FORECAST,
                AnalyticsType.PERIOD_COMPARISON
        );
    }

    public boolean requires(AnalyticsType type) {
        return analyticsTypes.contains(type);
    }

    public boolean hasComparison() {
        return compareFrom != null && compareTo != null;
    }

    public AnalyticsRequest toComparisonRequest() {
        AnalyticsRequest comparisonRequest = new AnalyticsRequest();
        comparisonRequest.setHouseholdRefId(this.householdRefId);
        // 👇 comparison period becomes the PRIMARY period
        comparisonRequest.setFrom(this.compareFrom);
        comparisonRequest.setTo(this.compareTo);
        comparisonRequest.setAnalyticsTypes(this.analyticsTypes);
        // 👇 DO NOT allow nested comparison
        comparisonRequest.setCompareFrom(null);
        comparisonRequest.setCompareTo(null);
        return comparisonRequest;
    }

}
