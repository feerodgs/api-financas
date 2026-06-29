package com.feliperodrigues.apifinancas.controller;

import com.feliperodrigues.apifinancas.config.OpenApiConfig;
import com.feliperodrigues.apifinancas.domain.User;
import com.feliperodrigues.apifinancas.dto.transaction.CreateTransactionRequest;
import com.feliperodrigues.apifinancas.dto.transaction.TransactionResponse;
import com.feliperodrigues.apifinancas.dto.transaction.UpdateTransactionRequest;
import com.feliperodrigues.apifinancas.exception.ErrorResponse;
import com.feliperodrigues.apifinancas.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Tag(name = "Transações", description = "Gerenciamento de receitas e despesas")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    @Operation(summary = "Registrar transação",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Transação criada"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Conta ou categoria não encontrada",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    public ResponseEntity<TransactionResponse> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateTransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.create(user, request));
    }

    @GetMapping
    @Operation(summary = "Listar transações com filtros e paginação")
    public ResponseEntity<Page<TransactionResponse>> findAll(
            @AuthenticationPrincipal User user,
            @Parameter(description = "Filtrar por conta") @RequestParam(required = false) Long accountId,
            @Parameter(description = "Filtrar por categoria") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "Data inicial (yyyy-MM-dd)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Data final (yyyy-MM-dd)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 20, sort = "date") Pageable pageable) {
        return ResponseEntity.ok(transactionService.findAll(user, accountId, categoryId, startDate, endDate, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar transação por ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Transação encontrada"),
                    @ApiResponse(responseCode = "404", description = "Transação não encontrada",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    public ResponseEntity<TransactionResponse> findById(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        return ResponseEntity.ok(transactionService.findById(user, id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar transação",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Transação atualizada"),
                    @ApiResponse(responseCode = "404", description = "Transação não encontrada",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    public ResponseEntity<TransactionResponse> update(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody UpdateTransactionRequest request) {
        return ResponseEntity.ok(transactionService.update(user, id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover transação",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Transação removida"),
                    @ApiResponse(responseCode = "404", description = "Transação não encontrada",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        transactionService.delete(user, id);
        return ResponseEntity.noContent().build();
    }
}
