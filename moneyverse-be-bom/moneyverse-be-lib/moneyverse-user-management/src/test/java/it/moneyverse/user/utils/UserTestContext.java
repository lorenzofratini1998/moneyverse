package it.moneyverse.user.utils;

import it.moneyverse.test.model.TestContext;
import it.moneyverse.test.model.dto.ScriptMetadata;
import it.moneyverse.test.operations.mapping.EntityScriptGenerator;
import it.moneyverse.test.services.SQLScriptService;
import it.moneyverse.user.model.entities.Language;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class UserTestContext extends TestContext<UserTestContext> {

  private static UserTestContext currencyTestContext;
  private final List<Language> languages;

  public UserTestContext() {
    super();
    languages = new ArrayList<>();
    setCurrentInstance(this);
  }

  private static void setCurrentInstance(UserTestContext instance) {
    currencyTestContext = instance;
  }

  private static UserTestContext getCurrentInstance() {
    if (currencyTestContext == null) {
      throw new IllegalStateException("UserTestContext not initialized");
    }
    return currencyTestContext;
  }

  @Override
  public UserTestContext self() {
    return this;
  }

  @Override
  public UserTestContext generateScript(Path dir) {
    new EntityScriptGenerator(new ScriptMetadata(dir, languages), new SQLScriptService()).execute();
    return self();
  }
}
