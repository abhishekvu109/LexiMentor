package com.abhi.saarthi.auth.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Builder
@EqualsAndHashCode
@ToString
@Data
public class UserRoleDTO {
    private String refId;
    private String uuid;
    private String name;
    private String status;
    private String description;
}
