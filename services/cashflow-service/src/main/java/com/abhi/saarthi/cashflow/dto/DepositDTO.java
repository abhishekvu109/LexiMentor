package com.abhi.saarthi.cashflow.dto;

import lombok.*;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
@ToString
public class DepositDTO {
    private String refId;
    private String uuid;
    private String status;
    private double amount;
    private String username;
    private LocalDate depositDate;
    private String source;
    private String notes;
    private String householdRefId;
}
