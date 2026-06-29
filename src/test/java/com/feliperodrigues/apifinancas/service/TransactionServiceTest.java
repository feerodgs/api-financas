package com.feliperodrigues.apifinancas.service;

import com.feliperodrigues.apifinancas.domain.Account;
import com.feliperodrigues.apifinancas.domain.Category;
import com.feliperodrigues.apifinancas.domain.Transaction;
import com.feliperodrigues.apifinancas.domain.User;
import com.feliperodrigues.apifinancas.domain.enums.AccountType;
import com.feliperodrigues.apifinancas.domain.enums.CategoryType;
import com.feliperodrigues.apifinancas.domain.enums.TransactionType;
import com.feliperodrigues.apifinancas.dto.transaction.CreateTransactionRequest;
import com.feliperodrigues.apifinancas.exception.ApiException;
import com.feliperodrigues.apifinancas.repository.AccountRepository;
import com.feliperodrigues.apifinancas.repository.CategoryRepository;
import com.feliperodrigues.apifinancas.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock TransactionRepository transactionRepository;
    @Mock AccountRepository accountRepository;
    @Mock CategoryRepository categoryRepository;
    @InjectMocks TransactionService transactionService;

    private User user;
    private Account account;
    private Category category;

    @BeforeEach
    void setup() {
        user = User.builder().id(1L).name("Felipe").email("test@test.com").password("encoded").build();
        account = Account.builder().id(1L).user(user).name("Conta Corrente").type(AccountType.CHECKING).balance(BigDecimal.ZERO).build();
        category = Category.builder().id(1L).user(user).name("Alimentação").type(CategoryType.EXPENSE).build();
    }

    @Test
    void create_withValidData_returnsTransactionResponse() {
        var request = new CreateTransactionRequest(1L, 1L, "Almoço", new BigDecimal("35.50"), TransactionType.EXPENSE, LocalDate.now());
        var saved = Transaction.builder()
                .id(1L).user(user).account(account).category(category)
                .description("Almoço").amount(new BigDecimal("35.50"))
                .type(TransactionType.EXPENSE).date(LocalDate.now()).build();

        when(accountRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(account));
        when(categoryRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(category));
        when(transactionRepository.save(any())).thenReturn(saved);

        var response = transactionService.create(user, request);

        assertThat(response.description()).isEqualTo("Almoço");
        assertThat(response.amount()).isEqualByComparingTo(new BigDecimal("35.50"));
        assertThat(response.type()).isEqualTo(TransactionType.EXPENSE);
        assertThat(response.accountName()).isEqualTo("Conta Corrente");
        assertThat(response.categoryName()).isEqualTo("Alimentação");
    }

    @Test
    void create_withNullCategory_returnsTransactionWithoutCategory() {
        var request = new CreateTransactionRequest(1L, null, "Saque", new BigDecimal("100.00"), TransactionType.EXPENSE, LocalDate.now());
        var saved = Transaction.builder()
                .id(1L).user(user).account(account).category(null)
                .description("Saque").amount(new BigDecimal("100.00"))
                .type(TransactionType.EXPENSE).date(LocalDate.now()).build();

        when(accountRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(account));
        when(transactionRepository.save(any())).thenReturn(saved);

        var response = transactionService.create(user, request);

        assertThat(response.categoryId()).isNull();
        assertThat(response.categoryName()).isNull();
    }

    @Test
    void create_withAccountNotFound_throwsApiException() {
        var request = new CreateTransactionRequest(99L, null, "Saque", BigDecimal.TEN, TransactionType.EXPENSE, LocalDate.now());
        when(accountRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.create(user, request))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Conta não encontrada");
    }

    @Test
    void create_withCategoryNotFound_throwsApiException() {
        var request = new CreateTransactionRequest(1L, 99L, "Almoço", BigDecimal.TEN, TransactionType.EXPENSE, LocalDate.now());
        when(accountRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(account));
        when(categoryRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.create(user, request))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Categoria não encontrada");
    }

    @Test
    void findById_whenFound_returnsTransactionResponse() {
        var transaction = Transaction.builder()
                .id(1L).user(user).account(account).category(category)
                .description("Almoço").amount(BigDecimal.TEN)
                .type(TransactionType.EXPENSE).date(LocalDate.now()).build();

        when(transactionRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(transaction));

        var response = transactionService.findById(user, 1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.description()).isEqualTo("Almoço");
    }

    @Test
    void findById_whenNotFound_throwsApiException() {
        when(transactionRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.findById(user, 99L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Transação não encontrada");
    }

    @Test
    void delete_whenFound_deletesTransaction() {
        var transaction = Transaction.builder()
                .id(1L).user(user).account(account).category(null)
                .description("Almoço").amount(BigDecimal.TEN)
                .type(TransactionType.EXPENSE).date(LocalDate.now()).build();

        when(transactionRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(transaction));

        transactionService.delete(user, 1L);

        verify(transactionRepository).delete(transaction);
    }

    @Test
    void delete_whenNotFound_throwsApiException() {
        when(transactionRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.delete(user, 99L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Transação não encontrada");
    }
}
