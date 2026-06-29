package com.feliperodrigues.apifinancas.service;

import com.feliperodrigues.apifinancas.domain.User;
import com.feliperodrigues.apifinancas.domain.enums.TransactionType;
import com.feliperodrigues.apifinancas.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock TransactionRepository transactionRepository;
    @InjectMocks ReportService reportService;

    private User user;

    @BeforeEach
    void setup() {
        user = User.builder().id(1L).name("Felipe").email("test@test.com").password("encoded").build();
    }

    @Test
    void getMonthlySummary_returnsCorrectTotalsAndBalance() {
        var income = new BigDecimal("5000.00");
        var expense = new BigDecimal("1800.00");
        List<Object[]> breakdown = new ArrayList<>();
        breakdown.add(new Object[]{1L, "Alimentação", TransactionType.EXPENSE, new BigDecimal("1800.00")});

        when(transactionRepository.sumByTypeAndPeriod(eq(1L), any(), any(), eq(TransactionType.INCOME))).thenReturn(income);
        when(transactionRepository.sumByTypeAndPeriod(eq(1L), any(), any(), eq(TransactionType.EXPENSE))).thenReturn(expense);
        when(transactionRepository.findCategoryBreakdown(eq(1L), any(), any())).thenReturn(breakdown);

        var result = reportService.getMonthlySummary(user, 6, 2026);

        assertThat(result.totalIncome()).isEqualByComparingTo("5000.00");
        assertThat(result.totalExpense()).isEqualByComparingTo("1800.00");
        assertThat(result.balance()).isEqualByComparingTo("3200.00");
        assertThat(result.month()).isEqualTo(6);
        assertThat(result.year()).isEqualTo(2026);
        assertThat(result.categories()).hasSize(1);
        assertThat(result.categories().get(0).categoryName()).isEqualTo("Alimentação");
    }

    @Test
    void getMonthlySummary_withNoTransactions_returnsZeroBalance() {
        when(transactionRepository.sumByTypeAndPeriod(eq(1L), any(), any(), eq(TransactionType.INCOME))).thenReturn(BigDecimal.ZERO);
        when(transactionRepository.sumByTypeAndPeriod(eq(1L), any(), any(), eq(TransactionType.EXPENSE))).thenReturn(BigDecimal.ZERO);
        when(transactionRepository.findCategoryBreakdown(eq(1L), any(), any())).thenReturn(List.of());

        var result = reportService.getMonthlySummary(user, 1, 2026);

        assertThat(result.balance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.categories()).isEmpty();
    }

    @Test
    void getPeriodSummary_returnsCorrectTotals() {
        var startDate = LocalDate.of(2026, 1, 1);
        var endDate = LocalDate.of(2026, 6, 30);
        var income = new BigDecimal("12000.00");
        var expense = new BigDecimal("8000.00");

        when(transactionRepository.sumByTypeAndPeriod(1L, startDate, endDate, TransactionType.INCOME)).thenReturn(income);
        when(transactionRepository.sumByTypeAndPeriod(1L, startDate, endDate, TransactionType.EXPENSE)).thenReturn(expense);
        when(transactionRepository.findCategoryBreakdown(1L, startDate, endDate)).thenReturn(List.of());

        var result = reportService.getPeriodSummary(user, startDate, endDate);

        assertThat(result.totalIncome()).isEqualByComparingTo("12000.00");
        assertThat(result.totalExpense()).isEqualByComparingTo("8000.00");
        assertThat(result.balance()).isEqualByComparingTo("4000.00");
        assertThat(result.startDate()).isEqualTo(startDate);
        assertThat(result.endDate()).isEqualTo(endDate);
    }

    @Test
    void getPeriodSummary_withNegativeBalance_returnsNegativeValue() {
        var startDate = LocalDate.of(2026, 6, 1);
        var endDate = LocalDate.of(2026, 6, 30);

        when(transactionRepository.sumByTypeAndPeriod(1L, startDate, endDate, TransactionType.INCOME)).thenReturn(new BigDecimal("500.00"));
        when(transactionRepository.sumByTypeAndPeriod(1L, startDate, endDate, TransactionType.EXPENSE)).thenReturn(new BigDecimal("1500.00"));
        when(transactionRepository.findCategoryBreakdown(1L, startDate, endDate)).thenReturn(List.of());

        var result = reportService.getPeriodSummary(user, startDate, endDate);

        assertThat(result.balance()).isEqualByComparingTo("-1000.00");
    }
}
