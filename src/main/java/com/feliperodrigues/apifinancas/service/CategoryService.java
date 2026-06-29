package com.feliperodrigues.apifinancas.service;

import com.feliperodrigues.apifinancas.domain.Category;
import com.feliperodrigues.apifinancas.domain.User;
import com.feliperodrigues.apifinancas.domain.enums.CategoryType;
import com.feliperodrigues.apifinancas.dto.category.CategoryResponse;
import com.feliperodrigues.apifinancas.dto.category.CreateCategoryRequest;
import com.feliperodrigues.apifinancas.dto.category.UpdateCategoryRequest;
import com.feliperodrigues.apifinancas.exception.ApiException;
import com.feliperodrigues.apifinancas.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponse create(User user, CreateCategoryRequest request) {
        Category category = Category.builder()
                .user(user)
                .name(request.name())
                .type(request.type())
                .color(request.color())
                .build();
        return CategoryResponse.from(categoryRepository.save(category));
    }

    public List<CategoryResponse> findAll(User user, CategoryType type) {
        if (type != null) {
            return categoryRepository.findAllByUserIdAndType(user.getId(), type)
                    .stream()
                    .map(CategoryResponse::from)
                    .toList();
        }
        return categoryRepository.findAllByUserId(user.getId())
                .stream()
                .map(CategoryResponse::from)
                .toList();
    }

    public CategoryResponse findById(User user, Long id) {
        return categoryRepository.findByIdAndUserId(id, user.getId())
                .map(CategoryResponse::from)
                .orElseThrow(() -> new ApiException("Categoria não encontrada", HttpStatus.NOT_FOUND));
    }

    public CategoryResponse update(User user, Long id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ApiException("Categoria não encontrada", HttpStatus.NOT_FOUND));
        category.setName(request.name());
        category.setType(request.type());
        category.setColor(request.color());
        return CategoryResponse.from(categoryRepository.save(category));
    }

    public void delete(User user, Long id) {
        Category category = categoryRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ApiException("Categoria não encontrada", HttpStatus.NOT_FOUND));
        categoryRepository.delete(category);
    }
}
