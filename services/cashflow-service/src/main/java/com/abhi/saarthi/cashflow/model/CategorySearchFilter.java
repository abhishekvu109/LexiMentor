package com.abhi.saarthi.cashflow.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@Data
@ToString
@Builder
public class CategorySearchFilter {
    private String uuid;
    private String refId;
    private String name;
    private String status;
    private String sortBy = "name";
    private String sortDir = "asc";

    public static CategorySearchFilter defaultFilter() {
        return CategorySearchFilter.builder()
                .sortBy("name")
                .sortDir("asc")
                .build();
    }

    public boolean isEmpty() {
        return StringUtils.isAllEmpty(getUuid(), getRefId(), getName(), getStatus());
    }
}
