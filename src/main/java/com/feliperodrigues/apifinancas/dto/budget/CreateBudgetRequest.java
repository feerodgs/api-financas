package com.feliperodrigues.apifinancas.dto.budget;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateBudgetRequest(

        @NotNull(message = "Categoria é obrigatória")
        Long categoryId,

        @NotNull(message = "Valor é obrigatório")
        @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
        BigDecimal amount,

        @NotNull(message = "Mês é obrigatório")
        @Min(value = 1, message = "Mês deve ser entre 1 e 12")
        @Max(value = 12, message = "Mês deve ser entre 1 e 12")
        Short month,

        @NotNull(message = "Ano é obrigatório")
        @Min(value = 2000, message = "Ano inválido")
        Short year
) {}
