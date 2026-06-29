package com.feliperodrigues.apifinancas.service;

import com.feliperodrigues.apifinancas.domain.Budget;
import com.feliperodrigues.apifinancas.domain.Category;
import com.feliperodrigues.apifinancas.domain.User;
import com.feliperodrigues.apifinancas.dto.budget.BudgetResponse;
import com.feliperodrigues.apifinancas.dto.budget.CreateBudgetRequest;
import com.feliperodrigues.apifinancas.dto.budget.UpdateBudgetRequest;
import com.feliperodrigues.apifinancas.exception.ApiException;
import com.feliperodrigues.apifinancas.repository.BudgetRepository;
import com.feliperodrigues.apifinancas.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;

    public BudgetResponse create(User user, CreateBudgetRequest request) {
        if (budgetRepository.existsByUserIdAndCategoryIdAndMonthAndYear(
                user.getId(), request.categoryId(), request.month(), request.year())) {
            throw new ApiException("Já existe orçamento para essa categoria nesse mês/ano", HttpStatus.CONFLICT);
        }

        Category category = categoryRepository.findByIdAndUserId(request.categoryId(), user.getId())
                .orElseThrow(() -> new ApiException("Categoria não encontrada", HttpStatus.NOT_FOUND));

        Budget budget = Budget.builder()
                .user(user)
                .category(category)
                .amount(request.amount())
                .month(request.month())
                .year(request.year())
                .build();

        return BudgetResponse.from(budgetRepository.save(budget));
    }

    @Transactional(readOnly = true)
    public List<BudgetResponse> findAll(User user, Short month, Short year) {
        return budgetRepository.findAllByUserIdAndMonthAndYear(user.getId(), month, year)
                .stream()
                .map(BudgetResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public BudgetResponse findById(User user, Long id) {
        return budgetRepository.findByIdAndUserId(id, user.getId())
                .map(BudgetResponse::from)
                .orElseThrow(() -> new ApiException("Orçamento não encontrado", HttpStatus.NOT_FOUND));
    }

    public BudgetResponse update(User user, Long id, UpdateBudgetRequest request) {
        Budget budget = budgetRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ApiException("Orçamento não encontrado", HttpStatus.NOT_FOUND));
        budget.setAmount(request.amount());
        return BudgetResponse.from(budgetRepository.save(budget));
    }

    public void delete(User user, Long id) {
        Budget budget = budgetRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ApiException("Orçamento não encontrado", HttpStatus.NOT_FOUND));
        budgetRepository.delete(budget);
    }
}
