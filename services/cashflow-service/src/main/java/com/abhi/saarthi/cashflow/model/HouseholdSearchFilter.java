package com.abhi.saarthi.cashflow.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@Data
@ToString
@Builder
public class HouseholdSearchFilter {
    private String status;
    private String name;
    private String refId;
    private String currency;
    private String uuid;
    private String user;
    private String sortBy = "name";
    private String sortDir = "asc";

    public static HouseholdSearchFilter defaultFilter() {
        return HouseholdSearchFilter.builder()
                .sortBy("name")
                .sortDir("asc")
                .build();
    }

    public boolean isEmpty() {
        return StringUtils.isAllEmpty(status, name, refId, currency, uuid, user);
    }
}
