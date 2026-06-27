package com.abhi.saarthi.cashflow.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportFilter {
    private String owner;
    private String householdRefId;
    private LocalDate dateFrom;
    private LocalDate dateTo;
}
