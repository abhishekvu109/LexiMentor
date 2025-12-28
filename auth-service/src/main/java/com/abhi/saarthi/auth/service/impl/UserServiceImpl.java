package com.abhi.saarthi.auth.service.impl;

import com.abhi.saarthi.auth.constant.Status;
import com.abhi.saarthi.auth.dto.AppUser;
import com.abhi.saarthi.auth.dto.AuthResponse;
import com.abhi.saarthi.auth.entity.User;
import com.abhi.saarthi.auth.repository.UserRepository;
import com.abhi.saarthi.auth.service.ServiceUtil;
import com.abhi.saarthi.auth.service.UserService;
import com.abhi.saarthi.auth.util.KeyGeneratorUtil;
import com.abhi.saarthi.auth.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
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
        } catch (SignatureException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            log.warn("JWT Validation Error: {}", e.getMessage());
        }
        return new AuthResponse("UNAUTHORIZED");
    }
}
