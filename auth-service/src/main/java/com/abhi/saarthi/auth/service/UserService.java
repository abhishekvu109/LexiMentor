package com.abhi.saarthi.auth.service;

import com.abhi.saarthi.auth.dto.AppUser;
import com.abhi.saarthi.auth.dto.AuthResponse;

public interface UserService {
    AppUser save(AppUser user);
    AuthResponse validateToken(String token);
}
