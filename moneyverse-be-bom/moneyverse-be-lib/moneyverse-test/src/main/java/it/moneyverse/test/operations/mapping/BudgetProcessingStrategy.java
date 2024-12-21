package it.moneyverse.test.operations.mapping;

import it.moneyverse.core.model.entities.BudgetModel;
import it.moneyverse.test.model.TestContextModel;
import it.moneyverse.test.services.ScriptService;
import it.moneyverse.test.utils.helper.MapperTestHelper;

import java.util.List;

public class BudgetProcessingStrategy implements EntityProcessingStrategy {
  @Override
  public boolean supports(Class<?> clazz) {
    return BudgetModel.class.isAssignableFrom(clazz);
  }

  @Override
  public String process(TestContextModel testContext, ScriptService scriptService, Class<?> clazz) {
    List<?> budgets =
        testContext.getBudgets().stream().map(fake -> MapperTestHelper.map(fake, clazz)).toList();
    return scriptService.createInsertScript(budgets);
  }
}
