package it.moneyverse.budget.utils.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import it.moneyverse.budget.model.CategoryTestFactory;
import it.moneyverse.budget.model.dto.CategoryRequestDto;
import it.moneyverse.budget.model.dto.CategoryUpdateRequestDto;
import it.moneyverse.budget.model.entities.Category;
import it.moneyverse.budget.model.entities.DefaultCategory;
import it.moneyverse.core.model.dto.CategoryDto;
import it.moneyverse.test.utils.RandomUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Unit test for {@link CategoryMapper} */
class CategoryMapperTest {

  @Test
  void testToCategoryEntity_NullCategoryRequest() {
    assertNull(CategoryMapper.toCategory(null));
  }

  @Test
  void testToCategoryEntity_ValidCategoryRequest() {
    CategoryRequestDto request = CategoryTestFactory.CategoryRequestBuilder.defaultInstance();

    Category category = CategoryMapper.toCategory(request);

    assertEquals(request.userId(), category.getUserId());
    assertEquals(request.categoryName(), category.getCategoryName());
    assertEquals(request.description(), category.getDescription());
    assertNull(category.getParentCategory());
  }

  @Test
  void testToCategoryDto_NullCategoryEntity() {
    assertNull(CategoryMapper.toCategoryDto((Category) null));
  }

  @Test
  void testToCategoryDto_ValidCategoryEntity() {
    Category category = CategoryTestFactory.fakeCategory();

    CategoryDto dto = CategoryMapper.toCategoryDto(category);

    assertEquals(category.getCategoryId(), dto.getCategoryId());
    assertEquals(category.getUserId(), dto.getUserId());
    assertEquals(category.getCategoryName(), dto.getCategoryName());
    assertEquals(category.getDescription(), dto.getDescription());
    assertNull(dto.getParentCategory());
  }

  @Test
  void testToCategoryDto_CategoryEntityWithParentCategory() {
    Category parentCategory = CategoryTestFactory.fakeCategory();
    Category category = CategoryTestFactory.fakeCategory(parentCategory);

    CategoryDto dto = CategoryMapper.toCategoryDto(category);

    assertEquals(category.getCategoryId(), dto.getCategoryId());
    assertEquals(category.getUserId(), dto.getUserId());
    assertEquals(category.getCategoryName(), dto.getCategoryName());
    assertEquals(category.getDescription(), dto.getDescription());
    assertEquals(parentCategory.getCategoryId(), dto.getParentCategory());
  }

  @Test
  void testToCategoryDto_EmptyEntityList() {
    assertEquals(Collections.emptyList(), CategoryMapper.toCategoryDto(new ArrayList<>()));
  }

  @Test
  void testToCategoryDto_NonEmptyEntityList() {
    int entitiesCount = RandomUtils.randomInteger(0, 10);
    List<Category> categories = new ArrayList<>(entitiesCount);
    for (int i = 0; i < entitiesCount; i++) {
      categories.add(CategoryTestFactory.fakeCategory());
    }

    List<CategoryDto> categoryDtos = CategoryMapper.toCategoryDto(categories);

    for (int i = 0; i < entitiesCount; i++) {
      Category category = categories.get(i);
      CategoryDto categoryDto = categoryDtos.get(i);

      assertEquals(category.getCategoryId(), categoryDto.getCategoryId());
      assertEquals(category.getUserId(), categoryDto.getUserId());
      assertEquals(category.getCategoryName(), categoryDto.getCategoryName());
      assertEquals(category.getDescription(), categoryDto.getDescription());
    }
  }

  @Test
  void testToCategoryDto_NullDefaultCategory() {
    assertNull(CategoryMapper.toCategoryDto((DefaultCategory) null));
  }

  @Test
  void testToCategoryDto_DefaultCategory() {
    DefaultCategory defaultCategory = CategoryTestFactory.fakeDefaultCategory();

    CategoryDto dto = CategoryMapper.toCategoryDto(defaultCategory);

    assertEquals(defaultCategory.getId(), dto.getCategoryId());
    assertEquals(defaultCategory.getName(), dto.getCategoryName());
    assertEquals(defaultCategory.getDescription(), dto.getDescription());
  }

  @Test
  void testToCategoryDto_EmptyDefaultCategoryList() {
    assertEquals(Collections.emptyList(), CategoryMapper.mapDefaultCategories(new ArrayList<>()));
  }

  @Test
  void testToCategoryDto_NonEmptyDefaultCategoryList() {
    int entitiesCount = RandomUtils.randomInteger(0, 10);
    List<DefaultCategory> categories = new ArrayList<>(entitiesCount);
    for (int i = 0; i < entitiesCount; i++) {
      categories.add(CategoryTestFactory.fakeDefaultCategory());
    }

    List<CategoryDto> categoryDtos = CategoryMapper.mapDefaultCategories(categories);

    for (int i = 0; i < entitiesCount; i++) {
      DefaultCategory category = categories.get(i);
      CategoryDto categoryDto = categoryDtos.get(i);

      assertEquals(category.getId(), categoryDto.getCategoryId());
      assertEquals(category.getName(), categoryDto.getCategoryName());
      assertEquals(category.getDescription(), categoryDto.getDescription());
    }
  }

  @Test
  void testToCategory_PartialUpdate() {
    Category category = CategoryTestFactory.fakeCategory();
    CategoryUpdateRequestDto request =
        CategoryTestFactory.CategoryUpdateRequestBuilder.defaultInstance();

    Category result = CategoryMapper.partialUpdate(category, request);

    assertEquals(request.categoryName(), result.getCategoryName());
    assertEquals(request.description(), result.getDescription());
    assertNull(result.getParentCategory());
  }

  @Test
  void testToCategory_PartialUpdateWithParentCategory() {
    Category parentCategory = CategoryTestFactory.fakeCategory();
    Category category = CategoryTestFactory.fakeCategory(parentCategory);
    CategoryUpdateRequestDto request =
        CategoryTestFactory.CategoryUpdateRequestBuilder.builder()
            .withParentCategory(parentCategory.getCategoryId())
            .build();

    Category result = CategoryMapper.partialUpdate(category, request, parentCategory);

    assertEquals(request.categoryName(), result.getCategoryName());
    assertEquals(request.description(), result.getDescription());
    assertEquals(parentCategory.getCategoryId(), result.getParentCategory().getCategoryId());
  }
}
