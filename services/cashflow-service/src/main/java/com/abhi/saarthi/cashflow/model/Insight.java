package com.abhi.saarthi.cashflow.model;

import com.abhi.saarthi.cashflow.constants.Severity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class Insight {

    private String type;
    private String message;
    private Severity severity;

}
