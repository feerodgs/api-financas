package com.feliperodrigues.apifinancas.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String message,
        String path,
        Map<String, String> errors
) {
    public static ErrorResponse of(HttpStatus httpStatus, String message, String path) {
        return new ErrorResponse(LocalDateTime.now(), httpStatus.value(), message, path, null);
    }
}
