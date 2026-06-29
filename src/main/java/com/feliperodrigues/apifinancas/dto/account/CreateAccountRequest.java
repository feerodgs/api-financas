package com.feliperodrigues.apifinancas.dto.account;

import com.feliperodrigues.apifinancas.domain.enums.AccountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateAccountRequest(

        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
        String name,

        @NotNull(message = "Tipo é obrigatório")
        AccountType type,

        @NotNull(message = "Saldo inicial é obrigatório")
        @DecimalMin(value = "0.0", message = "Saldo não pode ser negativo")
        BigDecimal balance
) {}
