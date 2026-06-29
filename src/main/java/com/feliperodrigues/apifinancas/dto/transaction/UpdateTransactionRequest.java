package com.feliperodrigues.apifinancas.dto.transaction;

import com.feliperodrigues.apifinancas.domain.enums.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateTransactionRequest(

        @NotNull(message = "Conta é obrigatória")
        Long accountId,

        Long categoryId,

        @NotBlank(message = "Descrição é obrigatória")
        @Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres")
        String description,

        @NotNull(message = "Valor é obrigatório")
        @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
        BigDecimal amount,

        @NotNull(message = "Tipo é obrigatório")
        TransactionType type,

        @NotNull(message = "Data é obrigatória")
        LocalDate date
) {}
