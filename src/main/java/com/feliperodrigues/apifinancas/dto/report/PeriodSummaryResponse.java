package com.feliperodrigues.apifinancas.dto.report;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PeriodSummaryResponse(
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal balance,
        List<CategoryBreakdownItem> categories
) {}
