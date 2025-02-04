package it.moneyverse.budget.utils;

import static it.moneyverse.budget.enums.BudgetSortAttributeEnum.*;

import it.moneyverse.budget.enums.BudgetSortAttributeEnum;
import it.moneyverse.budget.model.dto.BudgetCriteria;
import it.moneyverse.budget.model.dto.BudgetDto;
import it.moneyverse.budget.model.dto.BudgetRequestDto;
import it.moneyverse.budget.model.entities.Budget;
import it.moneyverse.budget.model.entities.BudgetFactory;
import it.moneyverse.budget.model.entities.DefaultBudgetTemplate;
import it.moneyverse.core.enums.SortAttribute;
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

  private final List<Budget> budgets;
  private final List<DefaultBudgetTemplate> defaultBudgetTemplates;

  public BudgetTestContext(KeycloakContainer keycloakContainer) {
    super(keycloakContainer);
    budgets = BudgetFactory.createBudgets(getUsers());
    defaultBudgetTemplates = BudgetFactory.createDefaultBudgetTemplates();
    setCurrentInstance(this);
  }

  public BudgetTestContext() {
    super();
    budgets = BudgetFactory.createBudgets(getUsers());
    defaultBudgetTemplates = BudgetFactory.createDefaultBudgetTemplates();
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

  public List<Budget> getBudgets() {
    return budgets;
  }

  public List<Budget> getBudgets(UUID userId) {
    return budgets.stream().filter(budget -> userId.equals(budget.getUserId())).toList();
  }

  public List<DefaultBudgetTemplate> getDefaultBudgetTemplates() {
    return defaultBudgetTemplates;
  }

  public Budget getRandomBudget(UUID userId) {
    List<Budget> userBudgets =
        budgets.stream().filter(budget -> budget.getUserId().equals(userId)).toList();
    return userBudgets.get(RandomUtils.randomInteger(0, userBudgets.size() - 1));
  }

  public BudgetRequestDto createBudgetForUser(UUID userId) {
    return toBudgetRequest(BudgetFactory.fakeBudget(userId, budgets.size()));
  }

  private BudgetRequestDto toBudgetRequest(Budget budget) {
    return new BudgetRequestDto(
        budget.getUserId(),
        budget.getBudgetName(),
        budget.getDescription(),
        budget.getBudgetLimit(),
        budget.getAmount(),
        budget.getCurrency());
  }

  public BudgetDto getExpectedBudgetDto(BudgetRequestDto request) {
    return BudgetDto.builder()
        .withUserId(request.userId())
        .withBudgetName(request.budgetName())
        .withDescription(request.description())
        .withBudgetLimit(request.budgetLimit())
        .withAmount(request.amount())
        .withCurrency(request.currency())
        .build();
  }

  public int getBudgetsCount() {
    return budgets.size();
  }

  public List<Budget> filterBudgets(UUID userId, BudgetCriteria criteria) {
    return budgets.stream()
        .filter(budget -> userId.equals(budget.getUserId()))
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
          case BUDGET_NAME -> a.getBudgetName().compareTo(b.getBudgetName());
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
            new ScriptMetadata(dir, budgets, defaultBudgetTemplates), new SQLScriptService())
        .execute();
    return self();
  }
}
