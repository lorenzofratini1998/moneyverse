package it.moneyverse.budget.utils.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import it.moneyverse.budget.model.dto.BudgetDto;
import it.moneyverse.budget.model.dto.BudgetRequestDto;
import it.moneyverse.budget.model.entities.Budget;
import it.moneyverse.test.utils.RandomUtils;
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
    assertNull(BudgetMapper.toBudgetDto(null));
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
