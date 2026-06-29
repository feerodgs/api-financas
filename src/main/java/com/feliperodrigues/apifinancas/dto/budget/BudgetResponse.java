package com.feliperodrigues.apifinancas.dto.budget;

import com.feliperodrigues.apifinancas.domain.Budget;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BudgetResponse(
        Long id,
        Long categoryId,
        String categoryName,
        BigDecimal amount,
        Short month,
        Short year,
        LocalDateTime createdAt
) {
    public static BudgetResponse from(Budget b) {
        return new BudgetResponse(
                b.getId(),
                b.getCategory().getId(),
                b.getCategory().getName(),
                b.getAmount(),
                b.getMonth(),
                b.getYear(),
                b.getCreatedAt()
        );
    }
}
