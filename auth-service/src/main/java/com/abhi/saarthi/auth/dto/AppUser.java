package com.abhi.saarthi.auth.dto;

import lombok.Builder;

import java.util.Set;

@Builder
public record AppUser(String refId,String uuid,String username, String password, String status, String role, Set<UserRoleDTO> roles) {
}
