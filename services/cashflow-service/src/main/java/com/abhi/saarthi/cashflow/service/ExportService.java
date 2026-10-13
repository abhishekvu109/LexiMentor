package com.abhi.saarthi.cashflow.service;

import com.abhi.saarthi.cashflow.model.ExportFilter;

public interface ExportService {
    byte[] exportExpenses(ExportFilter filter);
    byte[] exportEarnings(ExportFilter filter);
    byte[] exportDeposits(ExportFilter filter);
}
