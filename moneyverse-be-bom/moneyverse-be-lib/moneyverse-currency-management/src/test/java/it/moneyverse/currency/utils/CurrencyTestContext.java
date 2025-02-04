package it.moneyverse.currency.utils;

import it.moneyverse.currency.model.CurrencyFactory;
import it.moneyverse.currency.model.entities.Currency;
import it.moneyverse.test.extensions.testcontainers.KeycloakContainer;
import it.moneyverse.test.model.TestContext;
import it.moneyverse.test.model.dto.ScriptMetadata;
import it.moneyverse.test.operations.mapping.EntityScriptGenerator;
import it.moneyverse.test.services.SQLScriptService;
import java.nio.file.Path;
import java.util.List;

public class CurrencyTestContext extends TestContext<CurrencyTestContext> {

  private static CurrencyTestContext currentInstance;

  private final List<Currency> currencies;

  public CurrencyTestContext(KeycloakContainer keycloakContainer) {
    super(keycloakContainer);
    currencies = CurrencyFactory.createCurrencies();
    setCurrentInstance(this);
  }

  public CurrencyTestContext() {
    super();
    currencies = CurrencyFactory.createCurrencies();
    setCurrentInstance(this);
  }

  private static void setCurrentInstance(CurrencyTestContext instance) {
    currentInstance = instance;
  }

  private static CurrencyTestContext getCurrentInstance() {
    if (currentInstance == null) {
      throw new IllegalStateException("TestContext instance is not set.");
    }
    return currentInstance;
  }

  public List<Currency> getCurrencies() {
    return currencies;
  }

  @Override
  public CurrencyTestContext self() {
    return this;
  }

  @Override
  public CurrencyTestContext generateScript(Path dir) {
    new EntityScriptGenerator(new ScriptMetadata(dir, currencies), new SQLScriptService())
        .execute();
    return self();
  }
}
