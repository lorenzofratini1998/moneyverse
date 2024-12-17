package it.moneyverse.test.model;

import it.moneyverse.core.enums.SortAttribute;
import it.moneyverse.core.enums.UserRoleEnum;
import it.moneyverse.core.model.dto.BoundCriteria;
import it.moneyverse.core.model.dto.PageCriteria;
import it.moneyverse.core.model.dto.SortCriteria;
import it.moneyverse.core.model.entities.AccountModel;
import it.moneyverse.core.model.entities.BudgetModel;
import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.test.enums.TestModelStrategyEnum;
import it.moneyverse.test.extensions.testcontainers.KeycloakContainer;
import it.moneyverse.test.model.dto.ScriptMetadata;
import it.moneyverse.test.model.dto.UserCredential;
import it.moneyverse.test.operations.keycloak.KeycloakTestSetupManager;
import it.moneyverse.test.operations.mapping.AccountProcessingStrategy;
import it.moneyverse.test.operations.mapping.BudgetProcessingStrategy;
import it.moneyverse.test.operations.mapping.EntityScriptGenerator;
import it.moneyverse.test.services.SQLScriptService;
import it.moneyverse.test.utils.RandomUtils;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.util.UriComponentsBuilder;

public abstract class TestContext {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestContext.class);

  private static TestContext currentInstance;

  protected final TestContextModel model;
  private final KeycloakTestSetupManager keycloakTestManager;

  protected TestContext(Builder<?> builder) {
    Instant now = Instant.now();
    model =
        new TestModelBuilder(builder.strategy)
            .buildTestModel(
                builder.withTestUsers, builder.withTestAccounts, builder.withTestBudgets);

    if (builder.keycloakContainer != null) {
      this.keycloakTestManager = new KeycloakTestSetupManager(builder.keycloakContainer, model);
      keycloakTestManager.setup();
    } else {
      this.keycloakTestManager = null;
    }

    if (builder.metadata != null) {
      new EntityScriptGenerator(model, builder.metadata, new SQLScriptService())
          .addStrategy(new AccountProcessingStrategy())
          .addStrategy(new BudgetProcessingStrategy())
          .execute();
    }
    setCurrentInstance(this);
    LOGGER.info("Test Context set up in {} ms", System.currentTimeMillis() - now.toEpochMilli());
  }

  protected static TestContext getCurrentInstance() {
    if (currentInstance == null) {
      throw new IllegalStateException("TestContext instance is not set.");
    }
    return currentInstance;
  }

  private static void setCurrentInstance(TestContext instance) {
    currentInstance = instance;
  }

  public TestContextModel getModel() {
    return model;
  }

  public UserModel getRandomUser() {
    List<UserModel> users =
        model.getUsers().stream()
            .filter(user -> !user.getRole().equals(UserRoleEnum.ADMIN))
            .toList();
    return users.get(RandomUtils.randomInteger(0, users.size() - 1));
  }

  public UserModel getAdminUser() {
    return model.getUsers().stream()
        .filter(user -> user.getRole().equals(UserRoleEnum.ADMIN))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No admin found"));
  }

  public String getAuthenticationToken(String username) {
    return keycloakTestManager.getTestAuthenticationToken(getUserCredential(username));
  }

  private UserCredential getUserCredential(String username) {
    return Optional.of(getUser(username))
        .map(user -> new UserCredential(user.getUsername(), user.getPassword()))
        .orElseThrow(() -> new IllegalArgumentException("No user found with username " + username));
  }

  private UserModel getUser(String username) {
    return model.getUsers().stream()
        .filter(user -> user.getUsername().equals(username))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No user found with username " + username));
  }

  public AccountModel getRandomAccount(String username) {
    List<AccountModel> userAccounts =
        model.getAccounts().stream()
            .filter(account -> account.getUsername().equals(username))
            .toList();
    return userAccounts.get(RandomUtils.randomInteger(0, userAccounts.size() - 1));
  }

  public BudgetModel getRandomBudget(String username) {
    List<BudgetModel> userBudgets =
            model.getBudgets().stream()
                    .filter(budget -> budget.getUsername().equals(username))
                    .toList();
    return userBudgets.get(RandomUtils.randomInteger(0, userBudgets.size() - 1));
  }

  protected boolean filterByBound(BigDecimal value, BoundCriteria boundCriteria) {
    return boundCriteria.getLower().map(lower -> value.compareTo(lower) >= 0).orElse(true)
            && boundCriteria.getUpper().map(upper -> value.compareTo(upper) <= 0).orElse(true);
  }

  public <T> String createUri(String path, T criteria) {
    UriComponentsBuilder uri = UriComponentsBuilder.fromUriString(path);
    for (Field field : criteria.getClass().getDeclaredFields()) {
      ReflectionUtils.makeAccessible(field);
      Object fieldValue = ReflectionUtils.getField(field, criteria);
      String fieldName = field.getName();

      if (fieldValue != null) {
        switch (fieldValue) {
          case BoundCriteria boundCriteria -> {
            boundCriteria
                .getLower()
                .ifPresent(lower -> uri.queryParam(fieldName + ".lower", lower));
            boundCriteria
                .getUpper()
                .ifPresent(upper -> uri.queryParam(fieldName + ".upper", upper));
          }
          case PageCriteria page -> {
            uri.queryParam(fieldName + ".offset", page.getOffset());
            uri.queryParam(fieldName + ".limit", page.getLimit());
          }
          case SortCriteria<? extends SortAttribute> sort -> {
            uri.queryParam(fieldName + ".attribute", sort.getAttribute());
            uri.queryParam(fieldName + ".direction", sort.getDirection());
          }
          default -> uri.queryParam(fieldName, fieldValue);
        }
      }
    }
    return uri.build().toUriString();
  }

  public abstract static class Builder<T extends Builder<T>> {

    private TestModelStrategyEnum strategy;
    private boolean withTestUsers;
    private boolean withTestAccounts;
    private boolean withTestBudgets;
    private KeycloakContainer keycloakContainer;
    private ScriptMetadata metadata;

    public T withStrategy(TestModelStrategyEnum strategy) {
      this.strategy = strategy;
      return self();
    }

    public T withTestUsers() {
      this.withTestUsers = true;
      return self();
    }

    public T withTestAccount() {
      this.withTestAccounts = true;
      return self();
    }

    public T withTestBudgets() {
      this.withTestBudgets = true;
      return self();
    }

    public T withKeycloak(KeycloakContainer keycloakContainer) {
      this.keycloakContainer = keycloakContainer;
      return self();
    }

    public T withScriptMetadata(ScriptMetadata metadata) {
      this.metadata = metadata;
      return self();
    }

    protected abstract T self();

    public abstract TestContext build();
  }
}
