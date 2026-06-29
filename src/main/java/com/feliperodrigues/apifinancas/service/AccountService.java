package com.feliperodrigues.apifinancas.service;

import com.feliperodrigues.apifinancas.domain.Account;
import com.feliperodrigues.apifinancas.domain.User;
import com.feliperodrigues.apifinancas.dto.account.AccountResponse;
import com.feliperodrigues.apifinancas.dto.account.CreateAccountRequest;
import com.feliperodrigues.apifinancas.dto.account.UpdateAccountRequest;
import com.feliperodrigues.apifinancas.exception.ApiException;
import com.feliperodrigues.apifinancas.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountResponse create(User user, CreateAccountRequest request) {
        Account account = Account.builder()
                .user(user)
                .name(request.name())
                .type(request.type())
                .balance(request.balance())
                .build();
        return AccountResponse.from(accountRepository.save(account));
    }

    public List<AccountResponse> findAll(User user) {
        return accountRepository.findAllByUserId(user.getId())
                .stream()
                .map(AccountResponse::from)
                .toList();
    }

    public AccountResponse findById(User user, Long id) {
        return accountRepository.findByIdAndUserId(id, user.getId())
                .map(AccountResponse::from)
                .orElseThrow(() -> new ApiException("Conta não encontrada", HttpStatus.NOT_FOUND));
    }

    public AccountResponse update(User user, Long id, UpdateAccountRequest request) {
        Account account = accountRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ApiException("Conta não encontrada", HttpStatus.NOT_FOUND));
        account.setName(request.name());
        account.setType(request.type());
        return AccountResponse.from(accountRepository.save(account));
    }

    public void delete(User user, Long id) {
        Account account = accountRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ApiException("Conta não encontrada", HttpStatus.NOT_FOUND));
        accountRepository.delete(account);
    }
}
