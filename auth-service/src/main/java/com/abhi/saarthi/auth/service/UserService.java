package com.abhi.saarthi.auth.service;

import com.abhi.saarthi.auth.dto.AppUser;
import com.abhi.saarthi.auth.dto.AuthResponse;
import com.abhi.saarthi.auth.dto.UserDTO;

import java.util.List;

public interface UserService {
    AppUser save(AppUser user);

    AuthResponse validateToken(String token);

    List<UserDTO> findAllUsers();
}
