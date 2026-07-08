package com.abhi.saarthi.auth.service;

import com.abhi.saarthi.auth.dto.AppUser;
import com.abhi.saarthi.auth.dto.AuthResponse;
import com.abhi.saarthi.auth.dto.UserDTO;
import com.abhi.saarthi.auth.entity.UserRole;

import java.util.List;
import java.util.Set;

public interface UserService {
    AppUser save(AppUser user);

    AppUser update(AppUser user);
    AuthResponse validateToken(String token);

    List<UserDTO> findAllUsers();
    Set<UserRole> getRolesByUsername(String username);
}
