package com.abhi.saarthi.cashflow.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@Data
@ToString
@Builder
public class BudgetSearchFilter {
    private String refId;
    private String uuid;
    private String sortBy = "year";
    private String sortDir = "desc";

    public static BudgetSearchFilter defaultFilter() {
        return BudgetSearchFilter.builder()
                .sortBy("year")
                .sortDir("desc")
                .build();
    }

    public boolean isEmpty() {
        return StringUtils.isAllEmpty(getUuid(), getRefId(), getSortBy(), getSortDir());
    }
}
