package com.feliperodrigues.apifinancas.repository;

import com.feliperodrigues.apifinancas.domain.Category;
import com.feliperodrigues.apifinancas.domain.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByUserId(Long userId);
    List<Category> findAllByUserIdAndType(Long userId, CategoryType type);
    Optional<Category> findByIdAndUserId(Long id, Long userId);
}
