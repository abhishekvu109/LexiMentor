package com.abhi.saarthi.auth.dto;

import lombok.Builder;

@Builder
public record UserDTO(String username, String status) {
}
