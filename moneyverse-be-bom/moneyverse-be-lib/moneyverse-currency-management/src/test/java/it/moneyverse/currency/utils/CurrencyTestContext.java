package it.moneyverse.currency.utils;

import it.moneyverse.currency.model.CurrencyFactory;
import it.moneyverse.currency.model.entities.Currency;
import it.moneyverse.test.extensions.testcontainers.KeycloakContainer;
import it.moneyverse.test.model.TestContext;
import it.moneyverse.test.model.dto.ScriptMetadata;
import it.moneyverse.test.operations.mapping.EntityScriptGenerator;
import it.moneyverse.test.services.SQLScriptService;
import it.moneyverse.test.utils.RandomUtils;
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

  public Currency getRandomCurrency() {
    List<Currency> filteredCurrencies = currencies.stream().filter(Currency::isEnabled).toList();
    return filteredCurrencies.get(RandomUtils.randomInteger(0, filteredCurrencies.size() - 1));
  }

  public Currency getRandomDisabledCurrency() {
    List<Currency> filteredCurrencies =
        currencies.stream().filter(currency -> !currency.isEnabled()).toList();
    return filteredCurrencies.get(RandomUtils.randomInteger(0, filteredCurrencies.size() - 1));
  }

  @Override
  public CurrencyTestContext self() {
    return this;
  }

  @Override
  public CurrencyTestContext generateScript(Path dir) {
    EntityScriptGenerator scriptGenerator =
        new EntityScriptGenerator(new ScriptMetadata(dir, currencies), new SQLScriptService());
    StringBuilder script = scriptGenerator.generateScript();
    scriptGenerator.save(script);
    return self();
  }
}
