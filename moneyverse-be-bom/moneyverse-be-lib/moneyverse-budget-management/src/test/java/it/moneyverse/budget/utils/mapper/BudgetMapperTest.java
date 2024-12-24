package it.moneyverse.budget.utils.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import it.moneyverse.budget.model.dto.BudgetDto;
import it.moneyverse.budget.model.dto.BudgetRequestDto;
import it.moneyverse.budget.model.dto.BudgetUpdateRequestDto;
import it.moneyverse.budget.model.entities.Budget;
import it.moneyverse.test.utils.RandomUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Unit test for {@link BudgetMapper} */
class BudgetMapperTest {

  @Test
  void testToBudgetEntity_NullBudgetRequest() {
    assertNull(BudgetMapper.toBudget(null));
  }

  @Test
  void testToBudgetEntity_ValidBudgetRequest() {
    BudgetRequestDto request =
        new BudgetRequestDto(
            RandomUtils.randomString(15),
            RandomUtils.randomString(15),
            RandomUtils.randomString(15),
            RandomUtils.randomBigDecimal(),
            RandomUtils.randomBigDecimal());

    Budget budget = BudgetMapper.toBudget(request);

    assertEquals(request.username(), budget.getUsername());
    assertEquals(request.budgetName(), budget.getBudgetName());
    assertEquals(request.description(), budget.getDescription());
    assertEquals(request.budgetLimit(), budget.getBudgetLimit());
    assertEquals(request.amount(), budget.getAmount());
  }

  @Test
  void testToBudgetDto_NullBudgetEntity() {
    assertNull(BudgetMapper.toBudgetDto((Budget) null));
  }

  @Test
  void testToBudgetDto_ValidBudgetEntity() {
    Budget budget = createBudget();

    BudgetDto dto = BudgetMapper.toBudgetDto(budget);

    assertEquals(budget.getBudgetId(), dto.getBudgetId());
    assertEquals(budget.getUsername(), dto.getUsername());
    assertEquals(budget.getBudgetName(), dto.getBudgetName());
    assertEquals(budget.getDescription(), dto.getDescription());
    assertEquals(budget.getBudgetLimit(), dto.getBudgetLimit());
    assertEquals(budget.getAmount(), dto.getAmount());
  }

  @Test
  void testToBudgetDto_EmptyEntityList() {
    assertEquals(Collections.emptyList(), BudgetMapper.toBudgetDto(new ArrayList<>()));
  }

  @Test
  void testToBudgetDto_NonEmptyEntityList() {
    int entitiesCount = RandomUtils.randomInteger(0, 10);
    List<Budget> budgets = new ArrayList<>(entitiesCount);
    for (int i = 0; i < entitiesCount; i++) {
      budgets.add(createBudget());
    }

    List<BudgetDto> budgetDtos = BudgetMapper.toBudgetDto(budgets);

    for (int i = 0; i < entitiesCount; i++) {
      Budget budget = budgets.get(i);
      BudgetDto budgetDto = budgetDtos.get(i);

      assertEquals(budget.getBudgetId(), budgetDto.getBudgetId());
      assertEquals(budget.getUsername(), budgetDto.getUsername());
      assertEquals(budget.getBudgetName(), budgetDto.getBudgetName());
      assertEquals(budget.getDescription(), budgetDto.getDescription());
      assertEquals(budget.getBudgetLimit(), budgetDto.getBudgetLimit());
      assertEquals(budget.getAmount(), budgetDto.getAmount());
    }
  }

  @Test
  void testToBudget_PartialUpdate() {
    Budget budget = createBudget();
    BudgetUpdateRequestDto request =
        new BudgetUpdateRequestDto(
            RandomUtils.randomString(15),
            RandomUtils.randomString(15),
            RandomUtils.randomBigDecimal(),
            RandomUtils.randomBigDecimal());

    Budget result = BudgetMapper.partialUpdate(budget, request);

    assertEquals(request.budgetName(), result.getBudgetName());
    assertEquals(request.description(), result.getDescription());
    assertEquals(request.budgetLimit(), result.getBudgetLimit());
    assertEquals(request.amount(), result.getAmount());
  }

  private Budget createBudget() {
    Budget budget = new Budget();
    budget.setBudgetId(RandomUtils.randomUUID());
    budget.setUsername(RandomUtils.randomString(15));
    budget.setBudgetName(RandomUtils.randomString(15));
    budget.setDescription(RandomUtils.randomString(15));
    budget.setBudgetLimit(RandomUtils.randomBigDecimal());
    budget.setAmount(RandomUtils.randomBigDecimal());
    return budget;
  }
}
