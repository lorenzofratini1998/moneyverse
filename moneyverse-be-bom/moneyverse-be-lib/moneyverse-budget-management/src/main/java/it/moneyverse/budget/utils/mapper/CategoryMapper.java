package it.moneyverse.budget.utils.mapper;

import it.moneyverse.budget.model.dto.CategoryDto;
import it.moneyverse.budget.model.dto.CategoryRequestDto;
import it.moneyverse.budget.model.dto.CategoryUpdateRequestDto;
import it.moneyverse.budget.model.entities.Category;
import it.moneyverse.budget.model.entities.DefaultCategory;
import java.util.Collections;
import java.util.List;

public class CategoryMapper {

  public static Category toCategory(CategoryRequestDto request) {
    if (request == null) {
      return null;
    }
    Category category = new Category();
    category.setUserId(request.userId());
    category.setCategoryName(request.categoryName());
    category.setDescription(request.description());
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
      return null;
    }
    if (request.categoryName() != null) {
      category.setCategoryName(request.categoryName());
    }
    if (request.description() != null) {
      category.setDescription(request.description());
    }
    return category;
  }

  private CategoryMapper() {}
}
