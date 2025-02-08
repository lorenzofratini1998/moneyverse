package it.moneyverse.budget.utils;

import static it.moneyverse.budget.enums.BudgetSortAttributeEnum.AMOUNT;
import static it.moneyverse.budget.enums.BudgetSortAttributeEnum.BUDGET_LIMIT;

import it.moneyverse.budget.enums.BudgetSortAttributeEnum;
import it.moneyverse.budget.model.dto.BudgetCriteria;
import it.moneyverse.budget.model.dto.CategoryDto;
import it.moneyverse.budget.model.dto.CategoryRequestDto;
import it.moneyverse.budget.model.entities.*;
import it.moneyverse.core.enums.SortAttribute;
import it.moneyverse.core.model.dto.PageCriteria;
import it.moneyverse.core.model.dto.SortCriteria;
import it.moneyverse.test.extensions.testcontainers.KeycloakContainer;
import it.moneyverse.test.model.TestContext;
import it.moneyverse.test.model.dto.ScriptMetadata;
import it.moneyverse.test.operations.mapping.EntityScriptGenerator;
import it.moneyverse.test.services.SQLScriptService;
import it.moneyverse.test.utils.RandomUtils;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort;

public class BudgetTestContext extends TestContext<BudgetTestContext> {

  private static BudgetTestContext currentInstance;

  private final List<Category> categories;
  private final List<DefaultCategory> defaultCategories;
  private final List<Budget> budgets;

  public BudgetTestContext(KeycloakContainer keycloakContainer) {
    super(keycloakContainer);
    categories = CategoryFactory.createCategories(getUsers());
    defaultCategories = CategoryFactory.createDefaultCategories();
    budgets = BudgetFactory.createBudgets(categories);
    setCurrentInstance(this);
  }

  public BudgetTestContext() {
    super();
    categories = CategoryFactory.createCategories(getUsers());
    defaultCategories = CategoryFactory.createDefaultCategories();
    budgets = BudgetFactory.createBudgets(categories);
    setCurrentInstance(this);
  }

  private static void setCurrentInstance(BudgetTestContext instance) {
    currentInstance = instance;
  }

  protected static BudgetTestContext getCurrentInstance() {
    if (currentInstance == null) {
      throw new IllegalStateException("TestContext instance is not set.");
    }
    return currentInstance;
  }

  public List<Category> getCategories() {
    return categories;
  }

  public List<Budget> getBudgets() {
    return budgets;
  }

  public List<Category> getCategories(UUID userId) {
    return categories.stream().filter(budget -> userId.equals(budget.getUserId())).toList();
  }

  public Category getRandomCategoryByUserId(UUID userId) {
    List<Category> userCategories =
        categories.stream().filter(category -> category.getUserId().equals(userId)).toList();
    return userCategories.get(RandomUtils.randomInteger(0, userCategories.size() - 1));
  }

  public Category getRandomCategoryByUserIdAndCategoryId(UUID userId, UUID categoryId) {
    Category category = getRandomCategoryByUserId(userId);
    if (category.getCategoryId().equals(categoryId)) {
      getRandomCategoryByUserIdAndCategoryId(userId, categoryId);
    }
    return category;
  }

  public Budget getRandomBudgetByUserId(UUID userId) {
    List<Budget> userBudgets =
        budgets.stream().filter(budget -> userId.equals(budget.getCategory().getUserId())).toList();
    return userBudgets.get(RandomUtils.randomInteger(0, userBudgets.size() - 1));
  }

  public List<DefaultCategory> getDefaultCategories() {
    return defaultCategories;
  }

  public CategoryRequestDto createCategoryForUser(UUID userId) {
    return toCategoryRequest(CategoryFactory.fakeCategory(userId, categories.size()));
  }

  private CategoryRequestDto toCategoryRequest(Category category) {
    return new CategoryRequestDto(
        category.getUserId(), null, category.getCategoryName(), category.getDescription());
  }

  public CategoryDto getExpectedCategoryDto(CategoryRequestDto request) {
    return CategoryDto.builder()
        .withUserId(request.userId())
        .withCategoryName(request.categoryName())
        .withDescription(request.description())
        .build();
  }

  public int getCategoriesCount() {
    return categories.size();
  }

  public List<Category> getCategoriesByUser(UUID userId, PageCriteria pageCriteria) {
    return categories.stream()
        .filter(category -> category.getUserId().equals(userId))
        .skip(pageCriteria.getOffset())
        .limit(pageCriteria.getLimit())
        .toList();
  }

  public List<Budget> filterBudgets(UUID userId, BudgetCriteria criteria) {
    return budgets.stream()
        .filter(budget -> userId.equals(budget.getCategory().getUserId()))
        .filter(
            budget ->
                criteria
                    .getAmount()
                    .map(
                        amountCriteria ->
                            budget.getAmount() != null
                                && filterByBound(budget.getAmount(), amountCriteria))
                    .orElse(true))
        .filter(
            budget ->
                criteria
                    .getBudgetLimit()
                    .map(
                        budgetLimitCriteria ->
                            budget.getBudgetLimit() != null
                                && filterByBound(budget.getBudgetLimit(), budgetLimitCriteria))
                    .orElse(true))
        .filter(
            budget ->
                criteria
                    .getCurrency()
                    .map(currency -> currency.equals(budget.getCurrency()))
                    .orElse(true))
        .filter(
            budget ->
                criteria.getDate().map(date -> date.matches(budget.getStartDate())).orElse(true))
        .sorted((a, b) -> sortByCriteria(a, b, criteria.getSort()))
        .skip(criteria.getPage().getOffset())
        .limit(criteria.getPage().getLimit())
        .toList();
  }

  private int sortByCriteria(
      Budget a, Budget b, SortCriteria<BudgetSortAttributeEnum> sortCriteria) {
    if (sortCriteria == null) {
      return 0;
    }

    SortAttribute attribute = sortCriteria.getAttribute();
    Sort.Direction direction = sortCriteria.getDirection();

    int comparison =
        switch (attribute) {
          case AMOUNT -> a.getAmount().compareTo(b.getAmount());
          case BUDGET_LIMIT -> a.getBudgetLimit().compareTo(b.getBudgetLimit());
          default -> 0;
        };

    return direction == Sort.Direction.ASC ? comparison : -comparison;
  }

  public BudgetCriteria createBudgetCriteria() {
    return new BudgetCriteriaRandomGenerator(getCurrentInstance()).generate();
  }

  @Override
  public BudgetTestContext self() {
    return this;
  }

  @Override
  public BudgetTestContext generateScript(Path dir) {
    new EntityScriptGenerator(
            new ScriptMetadata(dir, categories, defaultCategories, budgets), new SQLScriptService())
        .execute();
    return self();
  }
}
