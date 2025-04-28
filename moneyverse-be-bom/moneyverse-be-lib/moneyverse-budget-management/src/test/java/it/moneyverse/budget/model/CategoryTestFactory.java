package it.moneyverse.budget.model;

import it.moneyverse.budget.model.dto.CategoryRequestDto;
import it.moneyverse.budget.model.dto.CategoryUpdateRequestDto;
import it.moneyverse.budget.model.entities.Category;
import it.moneyverse.budget.model.entities.DefaultCategory;
import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.test.model.TestFactory;
import it.moneyverse.test.utils.RandomUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.openapitools.jackson.nullable.JsonNullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CategoryTestFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(CategoryTestFactory.class);

  public static List<Category> createCategories(List<UserModel> users) {
    List<Category> categories = new ArrayList<>();
    for (UserModel user : users) {
      List<Category> userCategories = new ArrayList<>();
      int numBudgetsPerUser =
          RandomUtils.randomInteger(
              TestFactory.MIN_CATEGORIES_PER_USER, TestFactory.MAX_CATEGORIES_PER_USER);
      for (int i = 0; i < numBudgetsPerUser; i++) {
        boolean isChild = userCategories.size() > 1 && RandomUtils.flipCoin();
        Category newCategory;
        if (isChild) {
          int parentIndex = RandomUtils.randomInteger(userCategories.size());
          newCategory = fakeCategory(user.getUserId(), i, userCategories.get(parentIndex));
        } else {
          newCategory = fakeCategory(user.getUserId(), i);
        }
        categories.add(newCategory);
        userCategories.add(newCategory);
      }
    }
    LOGGER.info("Created {} random categories for testing", categories.size());
    return categories;
  }

  public static List<DefaultCategory> createDefaultCategories() {
    List<DefaultCategory> defaultCategories = new ArrayList<>();
    for (int i = 0; i < TestFactory.DEFAULT_BUDGETS_PER_USER; i++) {
      defaultCategories.add(CategoryTestFactory.fakeDefaultCategory(i));
    }
    LOGGER.info("Created {} random default categories for testing", defaultCategories.size());
    return defaultCategories;
  }

  public static Category fakeCategory(UUID userId, Integer counter) {
    counter = counter + 1;
    Category category = new Category();
    category.setCategoryId(RandomUtils.randomUUID());
    category.setUserId(userId);
    category.setCategoryName("Category %s".formatted(counter));
    category.setDescription(RandomUtils.randomString(30));
    category.setStyle(TestFactory.fakeStyle());
    category.setCreatedBy(TestFactory.FAKE_USER);
    category.setCreatedAt(LocalDateTime.now());
    category.setUpdatedBy(TestFactory.FAKE_USER);
    category.setUpdatedAt(LocalDateTime.now());
    return category;
  }

  public static Category fakeCategory(UUID userId, Integer counter, Category parentCategory) {
    Category fakeCategory = fakeCategory(userId, counter);
    fakeCategory.setParentCategory(parentCategory);
    return fakeCategory;
  }

  public static Category fakeCategory(Category parentCategory) {
    Category category = fakeCategory();
    category.setParentCategory(parentCategory);
    return category;
  }

  public static Category fakeCategory() {
    Category category = new Category();
    category.setCategoryId(RandomUtils.randomUUID());
    category.setUserId(RandomUtils.randomUUID());
    category.setCategoryName(RandomUtils.randomString(15));
    category.setDescription(RandomUtils.randomString(15));
    category.setStyle(TestFactory.fakeStyle());
    return category;
  }

  public static DefaultCategory fakeDefaultCategory(Integer counter) {
    counter = counter + 1;
    DefaultCategory defaultCategory = new DefaultCategory();
    defaultCategory.setId(RandomUtils.randomUUID());
    defaultCategory.setName("Default Category %s".formatted(counter));
    defaultCategory.setDescription(RandomUtils.randomString(30));
    defaultCategory.setStyle(TestFactory.fakeStyle());
    return defaultCategory;
  }

  public static DefaultCategory fakeDefaultCategory() {
    DefaultCategory defaultCategory = new DefaultCategory();
    defaultCategory.setId(RandomUtils.randomUUID());
    defaultCategory.setName(RandomUtils.randomString(15));
    defaultCategory.setDescription(RandomUtils.randomString(15));
    defaultCategory.setStyle(TestFactory.fakeStyle());
    return defaultCategory;
  }

  public static class CategoryRequestBuilder {
    private UUID userId = RandomUtils.randomUUID();
    private UUID parentId = null;
    private String categoryName = RandomUtils.randomString(15);
    private final String description = RandomUtils.randomString(15);

    public CategoryRequestBuilder withUserId(UUID userId) {
      this.userId = userId;
      return this;
    }

    public static Stream<Supplier<CategoryRequestDto>> invalidCategoryRequestProvider() {
      return Stream.of(
          () -> CategoryRequestBuilder.builder().withNullUserId().build(),
          () -> CategoryRequestBuilder.builder().witNullCategoryName().build());
    }

    private CategoryRequestBuilder withNullUserId() {
      this.userId = null;
      return this;
    }

    private CategoryRequestBuilder witNullCategoryName() {
      this.categoryName = null;
      return this;
    }

    public CategoryRequestBuilder withParentId(UUID parentId) {
      this.parentId = parentId;
      return this;
    }

    public static CategoryRequestDto defaultInstance() {
      return builder().build();
    }

    public static CategoryRequestBuilder builder() {
      return new CategoryRequestBuilder();
    }

    public CategoryRequestDto build() {
      return new CategoryRequestDto(
          userId, parentId, categoryName, description, TestFactory.fakeStyleRequest());
    }
  }

  public static class CategoryUpdateRequestBuilder {
    private final String categoryName = RandomUtils.randomString(15);
    private final String description = RandomUtils.randomString(15);
    private UUID parentCategory = null;

    public CategoryUpdateRequestBuilder withParentCategory(UUID parentCategory) {
      this.parentCategory = parentCategory;
      return this;
    }

    public static CategoryUpdateRequestDto defaultInstance() {
      return builder().build();
    }

    public static CategoryUpdateRequestBuilder builder() {
      return new CategoryUpdateRequestBuilder();
    }

    public CategoryUpdateRequestDto build() {
      return new CategoryUpdateRequestDto(
          categoryName, description, parentCategory, TestFactory.fakeStyleRequest());
    }
  }
}
