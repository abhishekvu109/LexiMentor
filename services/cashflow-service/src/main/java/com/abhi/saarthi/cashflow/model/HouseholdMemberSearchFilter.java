package com.abhi.saarthi.cashflow.model;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
public class HouseholdMemberSearchFilter {
    private String sortBy = "joining_date";
    private String sortDir = "desc";
    private String uuid;
    private String refId;
    private String user;
    private String role;
    private String status;
    private LocalDateTime joiningDateFrom;
    private LocalDateTime joiningDateTo;
}
