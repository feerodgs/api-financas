package com.feliperodrigues.apifinancas.dto.report;

import com.feliperodrigues.apifinancas.domain.enums.TransactionType;

import java.math.BigDecimal;

public record CategoryBreakdownItem(
        Long categoryId,
        String categoryName,
        TransactionType type,
        BigDecimal total
) {}
