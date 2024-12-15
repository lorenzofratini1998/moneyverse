package it.moneyverse.test.operations.mapping;

import it.moneyverse.test.model.TestContextModel;
import it.moneyverse.test.services.ScriptService;

public interface EntityProcessingStrategy {

  boolean supports(Class<?> clazz);

  String process(TestContextModel testContext, ScriptService scriptService, Class<?> clazz);
}
