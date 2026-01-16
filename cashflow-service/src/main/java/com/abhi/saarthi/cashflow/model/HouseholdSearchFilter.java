package com.abhi.saarthi.cashflow.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class HouseholdSearchFilter {
    private String status;
    private String name;
    private String refId;
    private String currency;
    private String uuid;
    private String user;
    private String sortBy = "name";
    private String sortDir = "asc";
}
