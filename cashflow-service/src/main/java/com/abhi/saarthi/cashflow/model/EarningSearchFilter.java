package com.abhi.saarthi.cashflow.model;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
@Builder
public class EarningSearchFilter {
    private String refId;
    private String uuid;
    private String status;
    private String username;
    private Double amountFrom;
    private Double amountTo;
    private String source;
    private String sortBy = "depositData";
    private String sortDir = "desc";

    public static EarningSearchFilter defaultFilter() {
        return EarningSearchFilter.builder()
                .sortBy("depositData")
                .sortDir("desc")
                .build();
    }

    public boolean isEmpty() {
        return amountTo == null
                && amountFrom == null
                && StringUtils.isAllEmpty(getUuid(), getRefId(), getStatus(), getUsername(), getSource());
    }
}
