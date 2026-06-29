package com.feliperodrigues.apifinancas.service;

import com.feliperodrigues.apifinancas.domain.Account;
import com.feliperodrigues.apifinancas.domain.User;
import com.feliperodrigues.apifinancas.domain.enums.AccountType;
import com.feliperodrigues.apifinancas.dto.account.CreateAccountRequest;
import com.feliperodrigues.apifinancas.dto.account.UpdateAccountRequest;
import com.feliperodrigues.apifinancas.exception.ApiException;
import com.feliperodrigues.apifinancas.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock AccountRepository accountRepository;
    @InjectMocks AccountService accountService;

    private User user;

    @BeforeEach
    void setup() {
        user = User.builder().id(1L).name("Felipe").email("test@test.com").password("encoded").build();
    }

    @Test
    void create_withValidData_returnsAccountResponse() {
        var request = new CreateAccountRequest("Conta Corrente", AccountType.CHECKING, BigDecimal.ZERO);
        var saved = Account.builder().id(1L).user(user).name("Conta Corrente").type(AccountType.CHECKING).balance(BigDecimal.ZERO).build();
        when(accountRepository.save(any())).thenReturn(saved);

        var response = accountService.create(user, request);

        assertThat(response.name()).isEqualTo("Conta Corrente");
        assertThat(response.type()).isEqualTo(AccountType.CHECKING);
        assertThat(response.balance()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void findAll_returnsAllUserAccounts() {
        var account = Account.builder().id(1L).user(user).name("Poupança").type(AccountType.SAVINGS).balance(BigDecimal.TEN).build();
        when(accountRepository.findAllByUserId(1L)).thenReturn(List.of(account));

        var result = accountService.findAll(user);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Poupança");
    }

    @Test
    void findById_whenFound_returnsAccountResponse() {
        var account = Account.builder().id(1L).user(user).name("Carteira").type(AccountType.WALLET).balance(BigDecimal.ZERO).build();
        when(accountRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(account));

        var response = accountService.findById(user, 1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Carteira");
    }

    @Test
    void findById_whenNotFound_throwsApiException() {
        when(accountRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.findById(user, 99L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Conta não encontrada");
    }

    @Test
    void update_whenFound_returnsUpdatedResponse() {
        var account = Account.builder().id(1L).user(user).name("Antiga").type(AccountType.CHECKING).balance(BigDecimal.ZERO).build();
        var request = new UpdateAccountRequest("Nova", AccountType.SAVINGS);
        when(accountRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var response = accountService.update(user, 1L, request);

        assertThat(response.name()).isEqualTo("Nova");
        assertThat(response.type()).isEqualTo(AccountType.SAVINGS);
    }

    @Test
    void update_whenNotFound_throwsApiException() {
        when(accountRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.update(user, 99L, new UpdateAccountRequest("x", AccountType.CHECKING)))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Conta não encontrada");
    }

    @Test
    void delete_whenFound_deletesAccount() {
        var account = Account.builder().id(1L).user(user).name("Conta").type(AccountType.CHECKING).balance(BigDecimal.ZERO).build();
        when(accountRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(account));

        accountService.delete(user, 1L);

        verify(accountRepository).delete(account);
    }

    @Test
    void delete_whenNotFound_throwsApiException() {
        when(accountRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.delete(user, 99L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Conta não encontrada");
    }
}
