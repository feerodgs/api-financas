package com.feliperodrigues.apifinancas.service;

import com.feliperodrigues.apifinancas.domain.User;
import com.feliperodrigues.apifinancas.domain.enums.TransactionType;
import com.feliperodrigues.apifinancas.dto.report.CategoryBreakdownItem;
import com.feliperodrigues.apifinancas.dto.report.MonthlySummaryResponse;
import com.feliperodrigues.apifinancas.dto.report.PeriodSummaryResponse;
import com.feliperodrigues.apifinancas.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public MonthlySummaryResponse getMonthlySummary(User user, int month, int year) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        BigDecimal income = transactionRepository.sumByTypeAndPeriod(user.getId(), start, end, TransactionType.INCOME);
        BigDecimal expense = transactionRepository.sumByTypeAndPeriod(user.getId(), start, end, TransactionType.EXPENSE);
        List<CategoryBreakdownItem> categories = buildBreakdown(user.getId(), start, end);

        return new MonthlySummaryResponse(month, year, income, expense, income.subtract(expense), categories);
    }

    @Transactional(readOnly = true)
    public PeriodSummaryResponse getPeriodSummary(User user, LocalDate startDate, LocalDate endDate) {
        BigDecimal income = transactionRepository.sumByTypeAndPeriod(user.getId(), startDate, endDate, TransactionType.INCOME);
        BigDecimal expense = transactionRepository.sumByTypeAndPeriod(user.getId(), startDate, endDate, TransactionType.EXPENSE);
        List<CategoryBreakdownItem> categories = buildBreakdown(user.getId(), startDate, endDate);

        return new PeriodSummaryResponse(startDate, endDate, income, expense, income.subtract(expense), categories);
    }

    private List<CategoryBreakdownItem> buildBreakdown(Long userId, LocalDate start, LocalDate end) {
        return transactionRepository.findCategoryBreakdown(userId, start, end).stream()
                .map(row -> new CategoryBreakdownItem(
                        (Long) row[0],
                        (String) row[1],
                        (TransactionType) row[2],
                        (BigDecimal) row[3]
                ))
                .toList();
    }
}
