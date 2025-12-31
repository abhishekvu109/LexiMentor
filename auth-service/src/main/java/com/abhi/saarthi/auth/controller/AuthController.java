package com.abhi.saarthi.auth.controller;

import com.abhi.saarthi.auth.constant.ApplicationConstants;
import com.abhi.saarthi.auth.dto.*;
import com.abhi.saarthi.auth.entity.RefreshToken;
import com.abhi.saarthi.auth.entity.User;
import com.abhi.saarthi.auth.model.ResponseEntityBuilder;
import com.abhi.saarthi.auth.model.RestApiResponse;
import com.abhi.saarthi.auth.service.RefreshTokenService;
import com.abhi.saarthi.auth.service.UserService;
import com.abhi.saarthi.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@RequestMapping("/api/auth/v1")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService; // Injected UserService

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        if (authentication.isAuthenticated()) {
            User user = (User) authentication.getPrincipal();
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());
            String accessToken = jwtUtil.generateToken(user.getUsername());
            return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_CODE, new TokenResponse(accessToken, refreshToken.getToken()));
        } else {
            throw new UsernameNotFoundException("invalid user request..!!");
        }
    }

    @PostMapping(value = "/refresh", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> refreshToken(@RequestBody TokenRefreshRequest request) {
        RefreshToken refreshToken = refreshTokenService.findByToken(request.refreshToken())
                .map(refreshTokenService::verifyExpiration)
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));

        String accessToken = jwtUtil.generateToken(refreshToken.getUser().getUsername());
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_CODE, new TokenResponse(accessToken, request.refreshToken()));
    }

    @PostMapping(value = "/logout", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> logout(@RequestBody LogoutRequest request) {
        refreshTokenService.deleteByToken(request.token());
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_CODE, "Logged out successfully!");
    }

    @GetMapping("/validate")
    public ResponseEntity<AuthResponse> validateToken(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse("UNAUTHORIZED"));
        }
        
        String token = authHeader.substring(7);
        AuthResponse response = userService.validateToken(token);
        if ("AUTHORIZED".equals(response.getStatus())) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse("UNAUTHORIZED"));
    }
}

