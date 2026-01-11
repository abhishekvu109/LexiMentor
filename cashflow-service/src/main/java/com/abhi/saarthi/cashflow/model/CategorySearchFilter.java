package com.abhi.saarthi.cashflow.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CategorySearchFilter {
    private String uuid;
    private String refId;
    private String name;
    private String status;
    private String sortBy = "name";
    private String sortDir = "asc";
}
