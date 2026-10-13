package com.abhi.saarthi.cashflow.dto;

import com.abhi.saarthi.cashflow.constants.MemberRole;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Data
@EqualsAndHashCode
@ToString
public class HouseholdMemberDTO {
    private String uuid;
    private String refId;
    private String user;
    private String householdRefId;
    private MemberRole role;
    private String status;
    private LocalDateTime joiningDate;
}
