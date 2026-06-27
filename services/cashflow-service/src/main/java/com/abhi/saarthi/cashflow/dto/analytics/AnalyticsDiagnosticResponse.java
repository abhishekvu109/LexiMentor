package com.abhi.saarthi.cashflow.dto.analytics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnalyticsDiagnosticResponse {
    private AnalyticsSummary summary;
    private DiagnosticAnalytics diagnostic;
}
