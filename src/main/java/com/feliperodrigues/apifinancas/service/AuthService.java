package com.feliperodrigues.apifinancas.service;

import com.feliperodrigues.apifinancas.domain.User;
import com.feliperodrigues.apifinancas.dto.auth.AuthResponse;
import com.feliperodrigues.apifinancas.dto.auth.LoginRequest;
import com.feliperodrigues.apifinancas.dto.auth.RegisterRequest;
import com.feliperodrigues.apifinancas.exception.ApiException;
import com.feliperodrigues.apifinancas.repository.UserRepository;
import com.feliperodrigues.apifinancas.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ApiException("Email já cadastrado", HttpStatus.CONFLICT);
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        User saved = userRepository.save(user);

        String token = jwtTokenProvider.generateToken(saved);
        return AuthResponse.of(token, saved.getId(), saved.getName(), saved.getEmail());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ApiException("Usuário não encontrado", HttpStatus.NOT_FOUND));

        String token = jwtTokenProvider.generateToken(user);
        return AuthResponse.of(token, user.getId(), user.getName(), user.getEmail());
    }
}
