package it.moneyverse.budget.runtime.controllers;

import it.moneyverse.budget.model.dto.CategoryRequestDto;
import it.moneyverse.budget.model.dto.CategoryUpdateRequestDto;
import it.moneyverse.core.model.dto.CategoryDto;
import it.moneyverse.core.model.dto.PageCriteria;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

public interface CategoryOperations {
  CategoryDto createCategory(@Valid CategoryRequestDto request);

  List<CategoryDto> getCategories(Boolean defaultCategories);

  List<CategoryDto> getCategoriesByUser(UUID userId, PageCriteria pageCriteria);

  void createUserDefaultCategories(UUID userId);

  CategoryDto getCategory(UUID categoryId);

  CategoryDto updateCategory(UUID categoryId, @Valid CategoryUpdateRequestDto request);

  void deleteCategory(UUID categoryId);

  List<CategoryDto> getCategoryTreeByUserId(UUID userId);
}
