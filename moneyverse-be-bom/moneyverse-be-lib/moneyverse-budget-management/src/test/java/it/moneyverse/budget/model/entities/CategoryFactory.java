package it.moneyverse.budget.model.entities;

import static it.moneyverse.test.utils.FakeUtils.*;

import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.test.utils.RandomUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CategoryFactory {
  private static final Logger LOGGER = LoggerFactory.getLogger(CategoryFactory.class);

  public static List<Category> createCategories(List<UserModel> users) {
    List<Category> categories = new ArrayList<>();
    for (UserModel user : users) {
      List<Category> userCategories = new ArrayList<>();
      int numBudgetsPerUser =
          RandomUtils.randomInteger(MIN_CATEGORIES_PER_USER, MAX_CATEGORIES_PER_USER);
      for (int i = 0; i < numBudgetsPerUser; i++) {
        boolean isChild = !userCategories.isEmpty() && Math.random() < 0.5;
        Category newCategory;
        if (isChild) {
          int parentIndex = RandomUtils.randomInteger(0, userCategories.size() - 1);
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
    for (int i = 0; i < DEFAULT_BUDGETS_PER_USER; i++) {
      defaultCategories.add(CategoryFactory.fakeDefaultCategory(i));
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
    category.setCreatedBy(FAKE_USER);
    category.setCreatedAt(LocalDateTime.now());
    category.setUpdatedBy(FAKE_USER);
    category.setUpdatedAt(LocalDateTime.now());
    return category;
  }

  public static Category fakeCategory(UUID userId, Integer counter, Category parentCategory) {
    Category fakeCategory = fakeCategory(userId, counter);
    fakeCategory.setParentCategory(parentCategory);
    return fakeCategory;
  }

  public static DefaultCategory fakeDefaultCategory(Integer counter) {
    counter = counter + 1;
    DefaultCategory defaultCategory = new DefaultCategory();
    defaultCategory.setId(RandomUtils.randomUUID());
    defaultCategory.setName("Default Category %s".formatted(counter));
    defaultCategory.setDescription(RandomUtils.randomString(30));
    return defaultCategory;
  }
}
