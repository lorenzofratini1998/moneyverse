package it.moneyverse.budget.model;

import static it.moneyverse.budget.enums.BudgetSortAttributeEnum.AMOUNT;
import static it.moneyverse.budget.enums.BudgetSortAttributeEnum.BUDGET_LIMIT;

import it.moneyverse.budget.enums.BudgetSortAttributeEnum;
import it.moneyverse.budget.model.dto.BudgetCriteria;
import it.moneyverse.budget.model.dto.BudgetRequestDto;
import it.moneyverse.budget.model.dto.CategoryRequestDto;
import it.moneyverse.budget.model.entities.*;
import it.moneyverse.core.model.dto.*;
import it.moneyverse.test.extensions.testcontainers.KeycloakContainer;
import it.moneyverse.test.model.TestContext;
import it.moneyverse.test.model.dto.ScriptMetadata;
import it.moneyverse.test.operations.mapping.EntityScriptGenerator;
import it.moneyverse.test.services.SQLScriptService;
import it.moneyverse.test.utils.RandomUtils;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import org.springframework.data.domain.Sort;

public class BudgetTestContext extends TestContext<BudgetTestContext> {

  private static BudgetTestContext currentInstance;

  private final List<Category> categories;
  private final List<DefaultCategory> defaultCategories;
  private final List<Budget> budgets;

  public BudgetTestContext(KeycloakContainer keycloakContainer) {
    super(keycloakContainer);
    categories = CategoryTestFactory.createCategories(getUsers());
    defaultCategories = CategoryTestFactory.createDefaultCategories();
    budgets = BudgetTestFactory.createBudgets(categories);
    setCurrentInstance(this);
  }

  public BudgetTestContext() {
    super();
    categories = CategoryTestFactory.createCategories(getUsers());
    defaultCategories = CategoryTestFactory.createDefaultCategories();
    budgets = BudgetTestFactory.createBudgets(categories);
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
    return userCategories.get(RandomUtils.randomInteger(userCategories.size() - 1));
  }

  public BudgetRequestDto createBudgetRequest(Category category) {
    List<Budget> categoriesBudgets =
        budgets.stream().filter(budget -> budget.getCategory().equals(category)).toList();
    if (categoriesBudgets.isEmpty()) {
      return BudgetTestFactory.BudgetRequestBuilder.builder()
          .withCategoryId(category.getCategoryId())
          .build();
    }
    LocalDate startDate =
        categoriesBudgets.stream()
            .max(Comparator.comparing(Budget::getStartDate))
            .get()
            .getStartDate()
            .plusMonths(RandomUtils.randomInteger(1, 3));
    LocalDate endDate = startDate.plusMonths(RandomUtils.randomInteger(1, 3));
    return BudgetTestFactory.BudgetRequestBuilder.builder()
        .withCategoryId(category.getCategoryId())
        .withStartDate(startDate)
        .withEndDate(endDate)
        .build();
  }

  public Budget getRandomBudgetByUserId(UUID userId) {
    List<Budget> userBudgets =
        budgets.stream().filter(budget -> userId.equals(budget.getCategory().getUserId())).toList();
    return userBudgets.get(RandomUtils.randomInteger(userBudgets.size() - 1));
  }

  public List<DefaultCategory> getDefaultCategories() {
    return defaultCategories;
  }

  public CategoryRequestDto createCategoryForUser(UUID userId) {
    return toCategoryRequest(CategoryTestFactory.fakeCategory(userId, categories.size()));
  }

  private CategoryRequestDto toCategoryRequest(Category category) {
    return new CategoryRequestDto(
        category.getUserId(), null, category.getCategoryName(), category.getDescription());
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
        .filter(byUserId(userId))
        .filter(byAmount(criteria.getAmount()))
        .filter(byBudgetLimit(criteria.getBudgetLimit()))
        .filter(byCurrency(criteria.getCurrency()))
        .filter(byDate(criteria.getDate()))
        .sorted(sortByCriteria(criteria.getSort()))
        .skip(criteria.getPage().getOffset())
        .limit(criteria.getPage().getLimit())
        .toList();
  }

  private Predicate<Budget> byUserId(UUID userId) {
    return budget -> budget.getCategory().getUserId().equals(userId);
  }

  private Predicate<Budget> byAmount(Optional<BoundCriteria> amount) {
    return budget ->
        amount
            .map(
                amountCriteria ->
                    budget.getAmount() != null && filterByBound(budget.getAmount(), amountCriteria))
            .orElse(true);
  }

  private Predicate<Budget> byBudgetLimit(Optional<BoundCriteria> budgetLimit) {
    return budget ->
        budgetLimit
            .map(
                budgetLimitCriteria ->
                    budget.getBudgetLimit() != null
                        && filterByBound(budget.getBudgetLimit(), budgetLimitCriteria))
            .orElse(true);
  }

  private Predicate<Budget> byCurrency(Optional<String> currency) {
    return budget -> currency.map(curr -> curr.equals(budget.getCurrency())).orElse(true);
  }

  private Predicate<Budget> byDate(Optional<DateCriteria> dateBound) {
    return budget ->
        dateBound
            .map(
                date -> {
                  boolean matchesStart =
                      date.getStart().map(min -> !budget.getStartDate().isBefore(min)).orElse(true);
                  boolean matchesEnd =
                      date.getEnd().map(max -> !budget.getEndDate().isAfter(max)).orElse(true);
                  return matchesStart && matchesEnd;
                })
            .orElse(true);
  }

  private Comparator<Budget> sortByCriteria(SortCriteria<BudgetSortAttributeEnum> sortCriteria) {
    if (sortCriteria == null) {
      return (a, b) -> 0;
    }

    Comparator<Budget> comparator =
        switch (sortCriteria.getAttribute()) {
          case AMOUNT -> Comparator.comparing(Budget::getAmount);
          case BUDGET_LIMIT -> Comparator.comparing(Budget::getBudgetLimit);
          default -> (a, b) -> 0;
        };

    return sortCriteria.getDirection() == Sort.Direction.ASC ? comparator : comparator.reversed();
  }

  public BudgetCriteria createBudgetCriteria() {
    return BudgetTestFactory.BudgetCriteriaBuilder.generator(getCurrentInstance()).generate();
  }

  @Override
  public BudgetTestContext self() {
    return this;
  }

  @Override
  public BudgetTestContext generateScript(Path dir) {
    EntityScriptGenerator scriptGenerator =
        new EntityScriptGenerator(
            new ScriptMetadata(dir, categories, defaultCategories, budgets),
            new SQLScriptService());
    StringBuilder script = scriptGenerator.generateScript();
    scriptGenerator.save(script);
    return self();
  }
}
