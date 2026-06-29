package com.feliperodrigues.apifinancas.dto.category;

import com.feliperodrigues.apifinancas.domain.Category;
import com.feliperodrigues.apifinancas.domain.enums.CategoryType;

import java.time.LocalDateTime;

public record CategoryResponse(
        Long id,
        String name,
        CategoryType type,
        String color,
        LocalDateTime createdAt
) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getType(),
                category.getColor(),
                category.getCreatedAt()
        );
    }
}
