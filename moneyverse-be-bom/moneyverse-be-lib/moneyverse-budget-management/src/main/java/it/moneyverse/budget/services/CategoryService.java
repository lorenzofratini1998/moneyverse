package it.moneyverse.budget.services;

import it.moneyverse.budget.model.dto.CategoryDto;
import it.moneyverse.budget.model.dto.CategoryRequestDto;
import it.moneyverse.budget.model.dto.CategoryUpdateRequestDto;
import it.moneyverse.core.model.dto.PageCriteria;
import java.util.List;
import java.util.UUID;

public interface CategoryService {

  CategoryDto createCategory(CategoryRequestDto budgetDto);

  List<CategoryDto> getCategories(Boolean defaultCategories);

  List<CategoryDto> getCategoriesByUserId(UUID userId, PageCriteria pageCriteria);

  void createUserDefaultCategories(UUID userId);

  List<CategoryDto> getCategoryTreeByUserId(UUID userId);

  CategoryDto getCategory(UUID categoryId);

  CategoryDto updateCategory(UUID categoryId, CategoryUpdateRequestDto request);

  void deleteCategory(UUID categoryId);

  void deleteCategoriesByUserId(UUID userId);
}
