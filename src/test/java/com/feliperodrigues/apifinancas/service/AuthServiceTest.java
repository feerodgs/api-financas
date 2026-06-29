package com.feliperodrigues.apifinancas.service;

import com.feliperodrigues.apifinancas.domain.User;
import com.feliperodrigues.apifinancas.dto.auth.LoginRequest;
import com.feliperodrigues.apifinancas.dto.auth.RegisterRequest;
import com.feliperodrigues.apifinancas.exception.ApiException;
import com.feliperodrigues.apifinancas.repository.UserRepository;
import com.feliperodrigues.apifinancas.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtTokenProvider jwtTokenProvider;
    @Mock AuthenticationManager authenticationManager;
    @InjectMocks AuthService authService;

    @Test
    void register_withNewEmail_returnsAuthResponse() {
        var request = new RegisterRequest("Felipe", "test@test.com", "senha123");
        var saved = User.builder().id(1L).name("Felipe").email("test@test.com").password("encoded").build();

        when(userRepository.existsByEmail("test@test.com")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("encoded");
        when(userRepository.save(any())).thenReturn(saved);
        when(jwtTokenProvider.generateToken(any(User.class))).thenReturn("mock-token");

        var response = authService.register(request);

        assertThat(response.token()).isEqualTo("mock-token");
        assertThat(response.email()).isEqualTo("test@test.com");
        assertThat(response.type()).isEqualTo("Bearer");
        assertThat(response.userId()).isEqualTo(1L);
    }

    @Test
    void register_withExistingEmail_throwsConflict() {
        var request = new RegisterRequest("Felipe", "test@test.com", "senha123");

        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Email já cadastrado");
    }

    @Test
    void login_withValidCredentials_returnsAuthResponse() {
        var request = new LoginRequest("test@test.com", "senha123");
        var user = User.builder().id(1L).name("Felipe").email("test@test.com").password("encoded").build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateToken(any(User.class))).thenReturn("mock-token");

        var response = authService.login(request);

        assertThat(response.token()).isEqualTo("mock-token");
        assertThat(response.userId()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Felipe");
    }
}
