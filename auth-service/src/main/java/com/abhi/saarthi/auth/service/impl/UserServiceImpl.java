package com.abhi.saarthi.auth.service.impl;

import com.abhi.saarthi.auth.constant.ApplicationConstants;
import com.abhi.saarthi.auth.constant.ProfileType;
import com.abhi.saarthi.auth.constant.Status;
import com.abhi.saarthi.auth.dto.AppUser;
import com.abhi.saarthi.auth.dto.AuthResponse;
import com.abhi.saarthi.auth.dto.UserDTO;
import com.abhi.saarthi.auth.entity.User;
import com.abhi.saarthi.auth.entity.UserRole;
import com.abhi.saarthi.auth.repository.UserRepository;
import com.abhi.saarthi.auth.repository.UserRoleRepository;
import com.abhi.saarthi.auth.service.ServiceUtil;
import com.abhi.saarthi.auth.service.UserService;
import com.abhi.saarthi.auth.util.JwtUtil;
import com.abhi.saarthi.auth.util.KeyGeneratorUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public AppUser save(AppUser request) {
        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .status(Status.ApplicationStatus.ACTIVE)
                .uuid(KeyGeneratorUtil.uuid())
                .refId(KeyGeneratorUtil.refId())
                .profileType(ProfileType.MEMBER)
                .roles((CollectionUtils.isNotEmpty(request.roles()))
                        ? request.roles().stream().map(roleDTO -> userRoleRepository.findByNameIgnoreCase(roleDTO.getName()).orElseThrow(() -> new RuntimeException("Invalid role name passed."))).collect(Collectors.toSet())
                        : new HashSet<>(userRoleRepository.findByNameIn(ApplicationConstants.ROLES).orElseThrow(() -> new RuntimeException("Invalid role name passed.")))
                )
                .build();
        user = userRepository.save(user);
        return ServiceUtil.UserService.buildDTO(user);
    }

    @Override
    public AuthResponse validateToken(String token) {
        try {
            String username = jwtUtil.extractUsername(token);
            if (username != null && jwtUtil.validateToken(token, username)) {
                return new AuthResponse("AUTHORIZED");
            }
        } catch (SignatureException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException |
                 IllegalArgumentException e) {
            log.warn("JWT Validation Error: {}", e.getMessage());
        }
        return new AuthResponse("UNAUTHORIZED");
    }

    @Override
    public List<UserDTO> findAllUsers() {
        return userRepository.findAll().stream().map(user -> UserDTO.builder()
                .username(user.getUsername())
                .status(Status.ApplicationStatus.getStatusStr(user.getStatus()))
                .build()).toList();
    }

    @Override
    @Transactional
    public AppUser update(AppUser user) {
        User entity = userRepository.findByUsername(user.username()).orElseThrow(() -> new UsernameNotFoundException("username is not found"));
        entity.setPassword(passwordEncoder.encode(user.password()));
        entity.setStatus(Status.ApplicationStatus.getStatus(user.status()));
        entity.setRoles(user.roles().stream().map(roleDTO -> userRoleRepository.findByNameIgnoreCase(roleDTO.getName()).orElseThrow(() -> new RuntimeException("Invalid role name passed."))).collect(Collectors.toSet()));
        entity = userRepository.save(entity);
        return ServiceUtil.UserService.buildDTO(entity);
    }

    @Override
    public Set<UserRole> getRolesByUsername(String username) {
        Set<UserRole> userRoles = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User doesn't exist.")).getRoles();
        if (CollectionUtils.isEmpty(userRoles)) {
            userRoles.addAll(ApplicationConstants.ROLES.stream().map(role -> userRoleRepository.findByNameIgnoreCase(role).orElseThrow(() -> new RuntimeException("Role not found"))).collect(Collectors.toSet()));
        }
        return userRoles;
    }
}
