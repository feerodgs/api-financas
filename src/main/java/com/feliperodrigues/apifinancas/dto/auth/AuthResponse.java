package com.feliperodrigues.apifinancas.dto.auth;

public record AuthResponse(String token, String type, Long userId, String name, String email) {

    public static AuthResponse of(String token, Long userId, String name, String email) {
        return new AuthResponse(token, "Bearer", userId, name, email);
    }
}
