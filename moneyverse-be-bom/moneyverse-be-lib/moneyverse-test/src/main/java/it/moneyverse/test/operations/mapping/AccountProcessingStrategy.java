package it.moneyverse.test.operations.mapping;

import it.moneyverse.core.model.entities.AccountModel;
import it.moneyverse.test.model.TestContextModel;
import it.moneyverse.test.services.ScriptService;
import it.moneyverse.test.utils.helper.MapperTestHelper;
import java.util.List;

public class AccountProcessingStrategy implements EntityProcessingStrategy {

  @Override
  public boolean supports(Class<?> clazz) {
    return AccountModel.class.isAssignableFrom(clazz);
  }

  @Override
  public String process(TestContextModel model, ScriptService scriptService, Class<?> clazz) {
    List<?> accounts = model.getAccounts()
        .stream().map(fake -> MapperTestHelper.map(fake, clazz)).toList();
    return scriptService.createInsertScript(accounts);
  }
}
