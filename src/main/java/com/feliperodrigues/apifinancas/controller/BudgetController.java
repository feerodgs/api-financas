package com.feliperodrigues.apifinancas.controller;

import com.feliperodrigues.apifinancas.config.OpenApiConfig;
import com.feliperodrigues.apifinancas.domain.User;
import com.feliperodrigues.apifinancas.dto.budget.BudgetResponse;
import com.feliperodrigues.apifinancas.dto.budget.CreateBudgetRequest;
import com.feliperodrigues.apifinancas.dto.budget.UpdateBudgetRequest;
import com.feliperodrigues.apifinancas.exception.ErrorResponse;
import com.feliperodrigues.apifinancas.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/v1/budgets")
@RequiredArgsConstructor
@Tag(name = "Orçamentos", description = "Gerenciamento de orçamentos mensais por categoria")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    @Operation(summary = "Criar orçamento mensal",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Orçamento criado"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "Orçamento já existe para essa categoria/mês/ano",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    public ResponseEntity<BudgetResponse> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateBudgetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(budgetService.create(user, request));
    }

    @GetMapping
    @Operation(summary = "Listar orçamentos por mês e ano")
    public ResponseEntity<List<BudgetResponse>> findAll(
            @AuthenticationPrincipal User user,
            @Parameter(description = "Mês (1-12)", required = true) @RequestParam Short month,
            @Parameter(description = "Ano (ex: 2026)", required = true) @RequestParam Short year) {
        return ResponseEntity.ok(budgetService.findAll(user, month, year));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar orçamento por ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Orçamento encontrado"),
                    @ApiResponse(responseCode = "404", description = "Orçamento não encontrado",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    public ResponseEntity<BudgetResponse> findById(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        return ResponseEntity.ok(budgetService.findById(user, id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar valor do orçamento",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Orçamento atualizado"),
                    @ApiResponse(responseCode = "404", description = "Orçamento não encontrado",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    public ResponseEntity<BudgetResponse> update(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody UpdateBudgetRequest request) {
        return ResponseEntity.ok(budgetService.update(user, id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover orçamento",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Orçamento removido"),
                    @ApiResponse(responseCode = "404", description = "Orçamento não encontrado",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        budgetService.delete(user, id);
        return ResponseEntity.noContent().build();
    }
}
