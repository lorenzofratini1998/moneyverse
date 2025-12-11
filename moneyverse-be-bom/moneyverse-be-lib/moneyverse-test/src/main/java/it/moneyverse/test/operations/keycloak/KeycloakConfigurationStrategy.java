package it.moneyverse.test.operations.keycloak;

import it.moneyverse.core.model.entities.UserModel;
import java.util.List;
import org.keycloak.admin.client.Keycloak;

public interface KeycloakConfigurationStrategy {

  void configure(Keycloak client, List<UserModel> users);
}
