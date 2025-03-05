package it.moneyverse.budget.model;

import static it.moneyverse.test.model.TestFactory.FAKE_USER;

import it.moneyverse.budget.enums.BudgetSortAttributeEnum;
import it.moneyverse.budget.model.dto.BudgetCriteria;
import it.moneyverse.budget.model.dto.BudgetRequestDto;
import it.moneyverse.budget.model.dto.BudgetUpdateRequestDto;
import it.moneyverse.budget.model.entities.Budget;
import it.moneyverse.budget.model.entities.Category;
import it.moneyverse.core.model.dto.PageCriteria;
import it.moneyverse.core.model.dto.SortCriteria;
import it.moneyverse.test.model.TestFactory;
import it.moneyverse.test.utils.RandomUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;

public class BudgetTestFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(BudgetTestFactory.class);
  private static final Supplier<LocalDate> FAKE_START_DATE = () -> RandomUtils.randomDate();
  private static final Supplier<BigDecimal> FAKE_AMOUNT = () -> RandomUtils.randomBigDecimal();
  private static final Supplier<BigDecimal> FAKE_BUDGET_LIMIT =
      () -> RandomUtils.randomBigDecimal();
  private static final Supplier<String> FAKE_CURRENCY =
      () -> RandomUtils.randomString(3).toUpperCase();

  public static List<Budget> createBudgets(List<Category> categories) {
    List<Budget> budgets = new ArrayList<>();
    for (Category category : categories) {
      for (int i = 0;
          i
              < RandomUtils.randomInteger(
                  TestFactory.MIN_BUDGETS_PER_CATEGORY, TestFactory.MAX_BUDGETS_PER_CATEGORY);
          i++) {
        budgets.add(createBudget(category, budgets));
      }
    }
    LOGGER.info("Created {} random budgets for testing", budgets.size());
    return budgets;
  }

  private static Budget createBudget(Category category, List<Budget> budgets) {
    Budget fakeBudget = fakeBudget(category);
    if (budgets.stream()
        .anyMatch(
            b ->
                b.getCategory().getCategoryId().equals(fakeBudget.getCategory().getCategoryId())
                    && b.getStartDate().equals(fakeBudget.getStartDate())
                    && b.getEndDate().equals(fakeBudget.getEndDate()))) {
      return createBudget(category, budgets);
    }
    return fakeBudget;
  }

  public static Budget fakeBudget(Category category) {
    Budget budget = new Budget();
    budget.setBudgetId(RandomUtils.randomUUID());
    budget.setCategory(category);
    budget.setStartDate(FAKE_START_DATE.get());
    budget.setEndDate(budget.getStartDate().plusMonths(RandomUtils.randomInteger(1, 3)));
    budget.setAmount(FAKE_AMOUNT.get());
    budget.setBudgetLimit(FAKE_BUDGET_LIMIT.get());
    budget.setCurrency(FAKE_CURRENCY.get());
    budget.setCreatedBy(FAKE_USER);
    budget.setCreatedAt(LocalDateTime.now());
    budget.setUpdatedBy(FAKE_USER);
    budget.setUpdatedAt(LocalDateTime.now());
    return budget;
  }

  public static class BudgetRequestBuilder {
    private UUID categoryId = RandomUtils.randomUUID();
    private LocalDate startDate = FAKE_START_DATE.get();
    private LocalDate endDate = startDate.plusMonths(RandomUtils.randomInteger(1, 3));
    private final BigDecimal budgetLimit = FAKE_BUDGET_LIMIT.get();
    private final String currency = FAKE_CURRENCY.get();

    public BudgetRequestBuilder withCategoryId(UUID categoryId) {
      this.categoryId = categoryId;
      return this;
    }

    public BudgetRequestBuilder withStartDate(LocalDate startDate) {
      this.startDate = startDate;
      return this;
    }

    public BudgetRequestBuilder withEndDate(LocalDate endDate) {
      this.endDate = endDate;
      return this;
    }

    public static BudgetRequestDto defaultInstance() {
      return builder().build();
    }

    public static BudgetRequestBuilder builder() {
      return new BudgetRequestBuilder();
    }

    public BudgetRequestDto build() {
      return new BudgetRequestDto(categoryId, startDate, endDate, budgetLimit, currency);
    }
  }

  public static class BudgetUpdateRequestBuilder {
    private final LocalDate startDate = FAKE_START_DATE.get();
    private final LocalDate endDate = startDate.plusMonths(RandomUtils.randomInteger(1, 3));
    private final BigDecimal amount = FAKE_AMOUNT.get();
    private final BigDecimal budgetLimit = FAKE_BUDGET_LIMIT.get();
    private final String currency = FAKE_CURRENCY.get();

    public static BudgetUpdateRequestDto defaultInstance() {
      return builder().build();
    }

    public static BudgetUpdateRequestBuilder builder() {
      return new BudgetUpdateRequestBuilder();
    }

    public BudgetUpdateRequestDto build() {
      return new BudgetUpdateRequestDto(startDate, endDate, amount, budgetLimit, currency);
    }
  }

  public static class BudgetCriteriaBuilder {
    private BudgetTestContext testContext;

    public static BudgetCriteriaBuilder generator(BudgetTestContext testContext) {
      BudgetCriteriaBuilder builder = new BudgetCriteriaBuilder();
      builder.testContext = testContext;
      return builder;
    }

    public BudgetCriteria generate() {
      return composeCriteria().apply(new BudgetCriteria());
    }

    private Function<BudgetCriteria, BudgetCriteria> composeCriteria() {
      return withRandomAmount()
          .andThen(withRandomBudgetLimit())
          .andThen(withRandomCurrency())
          .andThen(withRandomDate())
          .andThen(withPage())
          .andThen(withSort());
    }

    private Function<BudgetCriteria, BudgetCriteria> withRandomAmount() {
      return criteria -> {
        criteria.setAmount(
            RandomUtils.flipCoin()
                ? TestFactory.fakeBoundCriteria(
                    testContext.getBudgets().stream().map(Budget::getAmount).toList())
                : null);
        return criteria;
      };
    }

    private Function<BudgetCriteria, BudgetCriteria> withRandomBudgetLimit() {
      return criteria -> {
        criteria.setBudgetLimit(
            RandomUtils.flipCoin()
                ? TestFactory.fakeBoundCriteria(
                    testContext.getBudgets().stream().map(Budget::getBudgetLimit).toList())
                : null);
        return criteria;
      };
    }

    private Function<BudgetCriteria, BudgetCriteria> withRandomCurrency() {
      return criteria -> {
        criteria.setCurrency(
            RandomUtils.flipCoin() ? RandomUtils.randomString(3).toUpperCase() : null);
        return criteria;
      };
    }

    private Function<BudgetCriteria, BudgetCriteria> withRandomDate() {
      return criteria -> {
        criteria.setDate(RandomUtils.flipCoin() ? TestFactory.fakeDateCriteria() : null);
        return criteria;
      };
    }

    private Function<BudgetCriteria, BudgetCriteria> withPage() {
      return criteria -> {
        criteria.setPage(new PageCriteria());
        return criteria;
      };
    }

    private Function<BudgetCriteria, BudgetCriteria> withSort() {
      return criteria -> {
        criteria.setSort(new SortCriteria<>(BudgetSortAttributeEnum.AMOUNT, Sort.Direction.ASC));
        return criteria;
      };
    }
  }

  private BudgetTestFactory() {}
}
