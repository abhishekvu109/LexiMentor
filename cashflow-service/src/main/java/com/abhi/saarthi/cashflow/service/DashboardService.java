package com.abhi.saarthi.cashflow.service;

import com.abhi.saarthi.cashflow.dto.dashboard.DashboardDTO;

public interface DashboardService {
    DashboardDTO buildDashboardOverview(String username);
}
