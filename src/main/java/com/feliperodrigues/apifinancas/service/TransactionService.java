package com.feliperodrigues.apifinancas.service;

import com.feliperodrigues.apifinancas.domain.Account;
import com.feliperodrigues.apifinancas.domain.Category;
import com.feliperodrigues.apifinancas.domain.Transaction;
import com.feliperodrigues.apifinancas.domain.User;
import com.feliperodrigues.apifinancas.dto.transaction.CreateTransactionRequest;
import com.feliperodrigues.apifinancas.dto.transaction.TransactionResponse;
import com.feliperodrigues.apifinancas.dto.transaction.UpdateTransactionRequest;
import com.feliperodrigues.apifinancas.exception.ApiException;
import com.feliperodrigues.apifinancas.repository.AccountRepository;
import com.feliperodrigues.apifinancas.repository.CategoryRepository;
import com.feliperodrigues.apifinancas.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    public TransactionResponse create(User user, CreateTransactionRequest request) {
        Account account = accountRepository.findByIdAndUserId(request.accountId(), user.getId())
                .orElseThrow(() -> new ApiException("Conta não encontrada", HttpStatus.NOT_FOUND));

        Category category = null;
        if (request.categoryId() != null) {
            category = categoryRepository.findByIdAndUserId(request.categoryId(), user.getId())
                    .orElseThrow(() -> new ApiException("Categoria não encontrada", HttpStatus.NOT_FOUND));
        }

        Transaction transaction = Transaction.builder()
                .user(user)
                .account(account)
                .category(category)
                .description(request.description())
                .amount(request.amount())
                .type(request.type())
                .date(request.date())
                .build();

        return TransactionResponse.from(transactionRepository.save(transaction));
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> findAll(User user,
                                             Long accountId,
                                             Long categoryId,
                                             LocalDate startDate,
                                             LocalDate endDate,
                                             Pageable pageable) {
        return transactionRepository
                .findFiltered(user.getId(), accountId, categoryId, startDate, endDate, pageable)
                .map(TransactionResponse::from);
    }

    @Transactional(readOnly = true)
    public TransactionResponse findById(User user, Long id) {
        return transactionRepository.findByIdAndUserId(id, user.getId())
                .map(TransactionResponse::from)
                .orElseThrow(() -> new ApiException("Transação não encontrada", HttpStatus.NOT_FOUND));
    }

    public TransactionResponse update(User user, Long id, UpdateTransactionRequest request) {
        Transaction transaction = transactionRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ApiException("Transação não encontrada", HttpStatus.NOT_FOUND));

        Account account = accountRepository.findByIdAndUserId(request.accountId(), user.getId())
                .orElseThrow(() -> new ApiException("Conta não encontrada", HttpStatus.NOT_FOUND));

        Category category = null;
        if (request.categoryId() != null) {
            category = categoryRepository.findByIdAndUserId(request.categoryId(), user.getId())
                    .orElseThrow(() -> new ApiException("Categoria não encontrada", HttpStatus.NOT_FOUND));
        }

        transaction.setAccount(account);
        transaction.setCategory(category);
        transaction.setDescription(request.description());
        transaction.setAmount(request.amount());
        transaction.setType(request.type());
        transaction.setDate(request.date());

        return TransactionResponse.from(transactionRepository.save(transaction));
    }

    public void delete(User user, Long id) {
        Transaction transaction = transactionRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ApiException("Transação não encontrada", HttpStatus.NOT_FOUND));
        transactionRepository.delete(transaction);
    }
}
