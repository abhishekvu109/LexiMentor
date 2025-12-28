package com.abhi.saarthi.auth.dto;

import lombok.Builder;

@Builder
public record AppUser(String username, String password, String status, String role) {
}
