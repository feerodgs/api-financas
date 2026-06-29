package com.feliperodrigues.apifinancas.controller;

import com.feliperodrigues.apifinancas.config.OpenApiConfig;
import com.feliperodrigues.apifinancas.domain.User;
import com.feliperodrigues.apifinancas.dto.account.AccountResponse;
import com.feliperodrigues.apifinancas.dto.account.CreateAccountRequest;
import com.feliperodrigues.apifinancas.dto.account.UpdateAccountRequest;
import com.feliperodrigues.apifinancas.exception.ErrorResponse;
import com.feliperodrigues.apifinancas.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Tag(name = "Contas", description = "Gerenciamento de contas financeiras")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @Operation(summary = "Criar conta",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Conta criada"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    public ResponseEntity<AccountResponse> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateAccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.create(user, request));
    }

    @GetMapping
    @Operation(summary = "Listar contas do usuário")
    public ResponseEntity<List<AccountResponse>> findAll(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(accountService.findAll(user));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar conta por ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Conta encontrada"),
                    @ApiResponse(responseCode = "404", description = "Conta não encontrada",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    public ResponseEntity<AccountResponse> findById(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        return ResponseEntity.ok(accountService.findById(user, id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar conta",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Conta atualizada"),
                    @ApiResponse(responseCode = "404", description = "Conta não encontrada",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    public ResponseEntity<AccountResponse> update(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody UpdateAccountRequest request) {
        return ResponseEntity.ok(accountService.update(user, id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover conta",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Conta removida"),
                    @ApiResponse(responseCode = "404", description = "Conta não encontrada",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        accountService.delete(user, id);
        return ResponseEntity.noContent().build();
    }
}
