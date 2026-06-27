package com.abhi.saarthi.cashflow.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Metric {
    private String name;
    private Object value;
}
