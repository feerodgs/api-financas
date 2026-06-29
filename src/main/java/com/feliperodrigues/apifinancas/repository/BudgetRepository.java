package com.feliperodrigues.apifinancas.repository;

import com.feliperodrigues.apifinancas.domain.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findAllByUserIdAndMonthAndYear(Long userId, Short month, Short year);
    Optional<Budget> findByIdAndUserId(Long id, Long userId);
    boolean existsByUserIdAndCategoryIdAndMonthAndYear(Long userId, Long categoryId, Short month, Short year);
}
