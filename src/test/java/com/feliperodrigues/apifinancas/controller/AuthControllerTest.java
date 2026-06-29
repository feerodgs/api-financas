package com.feliperodrigues.apifinancas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.feliperodrigues.apifinancas.dto.auth.AuthResponse;
import com.feliperodrigues.apifinancas.dto.auth.LoginRequest;
import com.feliperodrigues.apifinancas.dto.auth.RegisterRequest;
import com.feliperodrigues.apifinancas.security.JwtAuthenticationFilter;
import com.feliperodrigues.apifinancas.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean AuthService authService;
    @MockBean JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean UserDetailsService userDetailsService;

    @Test
    void register_withValidBody_returns201() throws Exception {
        var request = new RegisterRequest("Felipe", "test@test.com", "senha123");
        var response = AuthResponse.of("mock-token", 1L, "Felipe", "test@test.com");

        when(authService.register(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("mock-token"))
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    void register_withMissingName_returns400() throws Exception {
        var request = new RegisterRequest("", "test@test.com", "senha123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_withInvalidEmail_returns400() throws Exception {
        var request = new RegisterRequest("Felipe", "nao-e-email", "senha123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_withShortPassword_returns400() throws Exception {
        var request = new RegisterRequest("Felipe", "test@test.com", "123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_withValidBody_returns200() throws Exception {
        var request = new LoginRequest("test@test.com", "senha123");
        var response = AuthResponse.of("mock-token", 1L, "Felipe", "test@test.com");

        when(authService.login(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-token"))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void login_withMissingEmail_returns400() throws Exception {
        var request = new LoginRequest("", "senha123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
