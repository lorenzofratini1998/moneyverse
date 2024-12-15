package it.moneyverse.test.operations.keycloak;

import it.moneyverse.test.model.TestContextModel;
import org.keycloak.admin.client.Keycloak;

public interface KeycloakConfigurationStrategy {

  void configure(Keycloak client, TestContextModel testContext);
}
