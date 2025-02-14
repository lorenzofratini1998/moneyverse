package it.moneyverse.budget.utils.mapper;

import it.moneyverse.budget.model.dto.CategoryRequestDto;
import it.moneyverse.budget.model.dto.CategoryUpdateRequestDto;
import it.moneyverse.budget.model.entities.Category;
import it.moneyverse.budget.model.entities.DefaultCategory;
import it.moneyverse.core.model.dto.CategoryDto;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CategoryMapper {

  public static Category toCategory(UUID userId, DefaultCategory defaultCategory) {
    if (defaultCategory == null) {
      return null;
    }
    Category category = new Category();
    category.setUserId(userId);
    category.setCategoryName(defaultCategory.getName());
    category.setDescription(defaultCategory.getDescription());
    return category;
  }

  public static Category toCategory(CategoryRequestDto request) {
    return toCategory(request, null);
  }

  public static Category toCategory(CategoryRequestDto request, Category parentCategory) {
    if (request == null) {
      return null;
    }
    Category category = new Category();
    category.setUserId(request.userId());
    category.setCategoryName(request.categoryName());
    category.setDescription(request.description());
    category.setParentCategory(parentCategory);
    return category;
  }

  public static CategoryDto toCategoryDto(Category category) {
    if (category == null) {
      return null;
    }
    return CategoryDto.builder()
        .withCategoryId(category.getCategoryId())
        .withUserId(category.getUserId())
        .withCategoryName(category.getCategoryName())
        .withDescription(category.getDescription())
        .withParentCategory(
            category.getParentCategory() != null
                ? toCategoryDto(category.getParentCategory())
                : null)
        .build();
  }

  public static List<CategoryDto> toCategoryDto(List<Category> entities) {
    if (entities.isEmpty()) {
      return Collections.emptyList();
    }
    return entities.stream().map(CategoryMapper::toCategoryDto).toList();
  }

  public static CategoryDto toCategoryDto(DefaultCategory category) {
    if (category == null) {
      return null;
    }
    return CategoryDto.builder()
        .withCategoryId(category.getId())
        .withCategoryName(category.getName())
        .withDescription(category.getDescription())
        .build();
  }

  public static List<CategoryDto> mapDefaultCategories(List<DefaultCategory> entities) {
    if (entities.isEmpty()) {
      return Collections.emptyList();
    }
    return entities.stream().map(CategoryMapper::toCategoryDto).toList();
  }

  public static Category partialUpdate(Category category, CategoryUpdateRequestDto request) {
    if (request == null) {
      return category;
    }
    if (request.categoryName() != null) {
      category.setCategoryName(request.categoryName());
    }
    if (request.description() != null) {
      category.setDescription(request.description());
    }
    return category;
  }

  public static Category partialUpdate(
      Category category, CategoryUpdateRequestDto request, Category parentCategory) {
    category = partialUpdate(category, request);
    if (request.parentId().isPresent()) {
      category.setParentCategory(parentCategory);
    }
    return category;
  }

  private CategoryMapper() {}
}
