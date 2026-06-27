package com.abhi.saarthi.auth.controller;

import com.abhi.saarthi.auth.dto.AppUser;
import com.abhi.saarthi.auth.dto.LoginRequest;
import com.abhi.saarthi.auth.dto.LogoutRequest;
import com.abhi.saarthi.auth.dto.TokenRefreshRequest;
import com.abhi.saarthi.auth.dto.AuthResponse; // Added import
import com.abhi.saarthi.auth.dto.UserRoleDTO;
import com.abhi.saarthi.auth.entity.UserRole;
import com.abhi.saarthi.auth.model.RestApiResponse;
import com.abhi.saarthi.auth.repository.RefreshTokenRepository;
import com.abhi.saarthi.auth.repository.UserRepository;
import com.abhi.saarthi.auth.repository.UserRoleRepository;
import com.abhi.saarthi.auth.util.KeyGeneratorUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders; // Added import
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get; // Added import
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @BeforeEach
    void setUp() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
        
        if (userRoleRepository.findByNameIgnoreCase("USER").isEmpty()) {
            UserRole userRole = UserRole.builder()
                    .refId(KeyGeneratorUtil.refId())
                    .uuid(KeyGeneratorUtil.uuid())
                    .name("USER")
                    .description("User Role")
                    .build();
            userRoleRepository.save(userRole);
        }
    }
    
        @Test
        void testRegister() throws Exception {
            AppUser user = new AppUser("refid-test", "uuid-test", "testuser", "password", "ACTIVE", "USER", Set.of(UserRoleDTO.builder().refId("userrole-refid").uuid("userrole-uuid").name("USER").status("ACTIVE").description("User Role").build()));
            mockMvc.perform(post("/api/auth/v1/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(user)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.username").value("testuser"));
        }
    
        @Test
        void testLogin() throws Exception {
            AppUser user = new AppUser("refid-test", "uuid-test", "testuser", "password", "ACTIVE", "USER", Set.of(UserRoleDTO.builder().refId("userrole-refid").uuid("userrole-uuid").name("USER").status("ACTIVE").description("User Role").build()));
            mockMvc.perform(post("/api/auth/v1/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user)));
    
            LoginRequest loginRequest = new LoginRequest("testuser", "password");
    
            mockMvc.perform(post("/api/auth/v1/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.token").exists())
                    .andExpect(jsonPath("$.data.refreshToken").exists());
        }
    
        @Test
        void testRefreshToken() throws Exception {
            AppUser user = new AppUser("refid-test", "uuid-test", "testuser", "password", "ACTIVE", "USER", Set.of(UserRoleDTO.builder().refId("userrole-refid").uuid("userrole-uuid").name("USER").status("ACTIVE").description("User Role").build()));
            mockMvc.perform(post("/api/auth/v1/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user)));
    
            LoginRequest loginRequest = new LoginRequest("testuser", "password");
    
            MvcResult result = mockMvc.perform(post("/api/auth/v1/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andReturn();
            String responseString = result.getResponse().getContentAsString();
            RestApiResponse response = objectMapper.readValue(responseString, RestApiResponse.class);
            String refreshToken = objectMapper.convertValue(response.getData(), com.abhi.saarthi.auth.dto.TokenResponse.class).refreshToken();
    
            TokenRefreshRequest refreshRequest = new TokenRefreshRequest(refreshToken);
    
            mockMvc.perform(post("/api/auth/v1/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(refreshRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.token").exists())
                    .andExpect(jsonPath("$.data.refreshToken").value(refreshToken));
        }
    
        @Test
        void testLogout() throws Exception {
            AppUser user = new AppUser("refid-test", "uuid-test", "testuser", "password", "ACTIVE", "USER", Set.of(UserRoleDTO.builder().refId("userrole-refid").uuid("userrole-uuid").name("USER").status("ACTIVE").description("User Role").build()));
            mockMvc.perform(post("/api/auth/v1/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user)));
    
            LoginRequest loginRequest = new LoginRequest("testuser", "password");
    
            MvcResult result = mockMvc.perform(post("/api/auth/v1/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andReturn();
            String responseString = result.getResponse().getContentAsString();
            RestApiResponse response = objectMapper.readValue(responseString, RestApiResponse.class);
            String refreshToken = objectMapper.convertValue(response.getData(), com.abhi.saarthi.auth.dto.TokenResponse.class).refreshToken();
    
            LogoutRequest logoutRequest = new LogoutRequest(refreshToken);
    
            mockMvc.perform(post("/api/auth/v1/logout")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(logoutRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").value("Logged out successfully!"));
        }

        @Test
        void testValidateToken() throws Exception {
            // Register a user
            AppUser user = new AppUser("refid-test", "uuid-test", "testuser", "password", "ACTIVE", "USER", Set.of(UserRoleDTO.builder().refId("userrole-refid").uuid("userrole-uuid").name("USER").status("ACTIVE").description("User Role").build()));
            mockMvc.perform(post("/api/auth/v1/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user)));

            // Login to get a token
            LoginRequest loginRequest = new LoginRequest("testuser", "password");
            MvcResult result = mockMvc.perform(post("/api/auth/v1/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andReturn();
            String responseString = result.getResponse().getContentAsString();
            RestApiResponse loginResponse = objectMapper.readValue(responseString, RestApiResponse.class);
            String accessToken = objectMapper.convertValue(loginResponse.getData(), com.abhi.saarthi.auth.dto.TokenResponse.class).token();

            // Validate the token
            mockMvc.perform(get("/api/auth/v1/validate")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("AUTHORIZED"));

            // Test with an invalid token
            mockMvc.perform(get("/api/auth/v1/validate")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer invalid_token"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status").value("UNAUTHORIZED"));
            
            // Test with no token
            mockMvc.perform(get("/api/auth/v1/validate"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("UNAUTHORIZED"));
        }
    }
