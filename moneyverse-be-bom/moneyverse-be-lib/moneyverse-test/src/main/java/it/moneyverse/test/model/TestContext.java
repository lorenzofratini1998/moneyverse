package it.moneyverse.test.model;

import it.moneyverse.test.enums.TestModelStrategyEnum;
import it.moneyverse.test.extensions.testcontainers.KeycloakContainer;
import it.moneyverse.test.model.dto.ScriptMetadata;
import it.moneyverse.test.operations.keycloak.KeycloakTestSetupManager;
import it.moneyverse.test.operations.mapping.AccountProcessingStrategy;
import it.moneyverse.test.operations.mapping.EntityScriptGenerator;
import it.moneyverse.test.services.SQLScriptService;

public class TestContext {

  private final TestContextModel model;

  private final KeycloakTestSetupManager keycloakTestManager;

  public TestContextModel getModel() {
    return model;
  }

  public TestContext(Builder builder) {
    model =
        new TestModelBuilder(builder.strategy)
            .buildTestModel(builder.withTestUsers, builder.withTestAccounts);
    if (builder.keycloakContainer != null) {
      keycloakTestManager = new KeycloakTestSetupManager(builder.keycloakContainer, model);
      keycloakTestManager.setup();
    } else {
      keycloakTestManager = null;
    }
    new EntityScriptGenerator(model, builder.metadata, new SQLScriptService())
        .addStrategy(new AccountProcessingStrategy())
        .execute();
  }

  public static class Builder {

    private TestModelStrategyEnum strategy;
    private boolean withTestUsers;
    private boolean withTestAccounts;
    private KeycloakContainer keycloakContainer;
    private ScriptMetadata metadata;

    public Builder withStrategy(TestModelStrategyEnum strategy) {
      this.strategy = strategy;
      return this;
    }

    public Builder withTestUsers() {
      this.withTestUsers = true;
      return this;
    }

    public Builder withTestAccount() {
      this.withTestAccounts = true;
      return this;
    }

    public Builder withKeycloak(KeycloakContainer keycloakContainer) {
      this.keycloakContainer = keycloakContainer;
      return this;
    }

    public Builder withScriptMetadata(ScriptMetadata metadata) {
      this.metadata = metadata;
      return this;
    }

    public TestContext build() {
      return new TestContext(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  public String getRandomUserOrAdminUsername() {
    return keycloakTestManager.getRandomUser().getUsername();
  }

  public String getAdminUsername() {
    return keycloakTestManager.getAdminUser().getUsername();
  }

  public String getAuthenticationToken(String username) {
    return keycloakTestManager.getTestAuthenticationToken(model.getUserCredential(username));
  }

}
