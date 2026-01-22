package com.abhi.saarthi.cashflow.service;

import com.abhi.saarthi.cashflow.dto.DashboardOverviewResponse;

public interface DashboardService {
    DashboardOverviewResponse buildDashboardOverview(String username);
}
