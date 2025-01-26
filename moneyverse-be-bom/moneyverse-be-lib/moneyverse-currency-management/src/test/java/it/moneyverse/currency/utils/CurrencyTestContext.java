package it.moneyverse.currency.utils;

import it.moneyverse.currency.model.CurrencyFactory;
import it.moneyverse.currency.model.entities.Currency;
import it.moneyverse.test.model.TestContext;
import it.moneyverse.test.model.dto.ScriptMetadata;
import it.moneyverse.test.operations.mapping.EntityScriptGenerator;
import it.moneyverse.test.services.SQLScriptService;
import java.nio.file.Path;
import java.util.List;

public class CurrencyTestContext extends TestContext<CurrencyTestContext> {

  private static CurrencyTestContext currentInstance;

  private final List<Currency> currencies;

  public CurrencyTestContext() {
    super();
    currencies = CurrencyFactory.createCurrencies();
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
