package it.moneyverse.test.operations.keycloak;

import it.moneyverse.test.model.TestContextModel;
import java.util.ArrayList;
import java.util.List;
import org.keycloak.admin.client.Keycloak;

public class KeycloakTestSetupContext {

  private final List<KeycloakConfigurationStrategy> strategies = new ArrayList<>();

  public KeycloakTestSetupContext addStrategy(KeycloakConfigurationStrategy strategy) {
    strategies.add(strategy);
    return this;
  }

  public void execute(Keycloak client, TestContextModel context) {
    strategies.forEach(strategy -> strategy.configure(client, context));
  }

}
