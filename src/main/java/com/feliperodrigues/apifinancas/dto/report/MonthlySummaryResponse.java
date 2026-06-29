package com.feliperodrigues.apifinancas.dto.report;

import java.math.BigDecimal;
import java.util.List;

public record MonthlySummaryResponse(
        int month,
        int year,
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal balance,
        List<CategoryBreakdownItem> categories
) {}
