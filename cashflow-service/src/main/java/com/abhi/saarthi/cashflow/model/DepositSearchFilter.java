package com.abhi.saarthi.cashflow.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@Data
@Builder
@ToString
@EqualsAndHashCode
public class DepositSearchFilter {
    private String refId;
    private String uuid;
    private String status;
    private String username;
    private Double amountFrom;
    private Double amountTo;
    private String source;
    private String sortBy = "depositDate";
    private String sortDir = "desc";

    public static DepositSearchFilter defaultFilter() {
        return DepositSearchFilter.builder()
                .sortBy("depositDate")
                .sortDir("desc")
                .build();
    }

    public boolean isEmpty() {
        return amountTo == null
                && amountFrom == null
                && StringUtils.isAllEmpty(getUuid(), getRefId(), getStatus(), getUsername(), getSource());
    }
}
