package com.abhi.saarthi.cashflow.dto.analytics;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DiagnosticAnalytics {
    private MissingCategoryRate missingCategoryRate;
    private TransactionDensity transactionDensity;
    private List<CategoryOutlier> categoryOutliers;
    private PeakSpendDay peakSpendDay;
    private List<PaymentModeShare> cashVsCard;
}
