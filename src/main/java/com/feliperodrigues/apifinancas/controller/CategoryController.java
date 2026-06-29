package com.feliperodrigues.apifinancas.controller;

import com.feliperodrigues.apifinancas.config.OpenApiConfig;
import com.feliperodrigues.apifinancas.domain.User;
import com.feliperodrigues.apifinancas.domain.enums.CategoryType;
import com.feliperodrigues.apifinancas.dto.category.CategoryResponse;
import com.feliperodrigues.apifinancas.dto.category.CreateCategoryRequest;
import com.feliperodrigues.apifinancas.dto.category.UpdateCategoryRequest;
import com.feliperodrigues.apifinancas.exception.ErrorResponse;
import com.feliperodrigues.apifinancas.service.CategoryService;
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
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categorias", description = "Gerenciamento de categorias de transações")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @Operation(summary = "Criar categoria",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Categoria criada"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    public ResponseEntity<CategoryResponse> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(user, request));
    }

    @GetMapping
    @Operation(summary = "Listar categorias do usuário")
    public ResponseEntity<List<CategoryResponse>> findAll(
            @AuthenticationPrincipal User user,
            @Parameter(description = "Filtrar por tipo: INCOME ou EXPENSE")
            @RequestParam(required = false) CategoryType type) {
        return ResponseEntity.ok(categoryService.findAll(user, type));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar categoria por ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Categoria encontrada"),
                    @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    public ResponseEntity<CategoryResponse> findById(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        return ResponseEntity.ok(categoryService.findById(user, id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar categoria",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Categoria atualizada"),
                    @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    public ResponseEntity<CategoryResponse> update(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody UpdateCategoryRequest request) {
        return ResponseEntity.ok(categoryService.update(user, id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover categoria",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Categoria removida"),
                    @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        categoryService.delete(user, id);
        return ResponseEntity.noContent().build();
    }
}
