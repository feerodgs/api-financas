package com.feliperodrigues.apifinancas.dto.transaction;

import com.feliperodrigues.apifinancas.domain.Transaction;
import com.feliperodrigues.apifinancas.domain.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        Long accountId,
        String accountName,
        Long categoryId,
        String categoryName,
        String description,
        BigDecimal amount,
        TransactionType type,
        LocalDate date,
        LocalDateTime createdAt
) {
    public static TransactionResponse from(Transaction t) {
        return new TransactionResponse(
                t.getId(),
                t.getAccount().getId(),
                t.getAccount().getName(),
                t.getCategory() != null ? t.getCategory().getId() : null,
                t.getCategory() != null ? t.getCategory().getName() : null,
                t.getDescription(),
                t.getAmount(),
                t.getType(),
                t.getDate(),
                t.getCreatedAt()
        );
    }
}
