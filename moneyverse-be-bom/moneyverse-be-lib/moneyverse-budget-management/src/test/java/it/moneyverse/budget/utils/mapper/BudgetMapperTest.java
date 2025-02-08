package it.moneyverse.budget.utils.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import it.moneyverse.budget.model.dto.BudgetDto;
import it.moneyverse.budget.model.dto.BudgetRequestDto;
import it.moneyverse.budget.model.dto.BudgetUpdateRequestDto;
import it.moneyverse.budget.model.entities.Budget;
import it.moneyverse.budget.model.entities.Category;
import it.moneyverse.budget.utils.BudgetTestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BudgetMapperTest {

  @Mock Category category;

  @Test
  void testToBudgetEntity_NullBudgetRequest() {
    assertNull(BudgetMapper.toBudget(null, category));
  }

  @Test
  void testToBudgetEntity() {
    BudgetRequestDto request = BudgetTestUtils.createBudgetRequest();
    Budget budget = BudgetMapper.toBudget(request, category);

    assertEquals(request.budgetLimit(), budget.getBudgetLimit());
    assertEquals(request.currency(), budget.getCurrency());
    assertEquals(request.startDate(), budget.getStartDate());
    assertEquals(request.endDate(), budget.getEndDate());
    assertEquals(category, budget.getCategory());
  }

  @Test
  void testToBudgetDto_NullBudgetEntity() {
    assertNull(BudgetMapper.toBudgetDto((Budget) null));
  }

  @Test
  void testToBudgetDto() {
    Budget budget = BudgetTestUtils.createBudget(category);
    BudgetDto dto = BudgetMapper.toBudgetDto(budget);

    assertEquals(budget.getBudgetId(), dto.getBudgetId());
    assertEquals(budget.getStartDate(), dto.getStartDate());
    assertEquals(budget.getEndDate(), dto.getEndDate());
    assertEquals(budget.getBudgetLimit(), dto.getBudgetLimit());
    assertEquals(budget.getCurrency(), dto.getCurrency());
  }

  @Test
  void testToBudget_PartialUpdate_NullRequest() {
    assertNull(BudgetMapper.partialUpdate(null, null));
  }

  @Test
  void testToBudget_PartialUpdate() {
    Budget budget = BudgetTestUtils.createBudget(category);
    BudgetUpdateRequestDto request = BudgetTestUtils.createBudgetUpdateRequest();
    Budget result = BudgetMapper.partialUpdate(budget, request);

    assertEquals(budget.getBudgetId(), result.getBudgetId());
    assertEquals(request.amount(), result.getAmount());
    assertEquals(request.budgetLimit(), result.getBudgetLimit());
    assertEquals(request.currency(), result.getCurrency());
    assertEquals(request.startDate(), result.getStartDate());
    assertEquals(request.endDate(), result.getEndDate());
  }
}
