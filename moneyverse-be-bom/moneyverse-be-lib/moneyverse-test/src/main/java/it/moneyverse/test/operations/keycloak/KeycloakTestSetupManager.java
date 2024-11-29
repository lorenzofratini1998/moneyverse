package it.moneyverse.test.operations.keycloak;

import it.moneyverse.test.extensions.testcontainers.KeycloakContainer;
import it.moneyverse.test.model.TestContextModel;
import org.keycloak.admin.client.Keycloak;

public class KeycloakTestSetupManager {

  private final KeycloakContainer keycloakContainer;
  private final KeycloakTestSetupContext keycloakContext;

  public KeycloakTestSetupManager(KeycloakContainer keycloakContainer) {
    this.keycloakContainer = keycloakContainer;
    this.keycloakContext = new KeycloakTestSetupContext()
        .addStrategy(new CreateTestRealmStrategy())
        .addStrategy(new CreateTestUserRolesStrategy())
        .addStrategy(new CreateTestUsersStrategy());
  }

  public void setup(TestContextModel model) {
    try {
      Keycloak keycloakAdminClient = keycloakContainer.getKeycloakAdminClient();
      keycloakContext.execute(keycloakAdminClient, model);
    } catch (Exception e) {
      throw new RuntimeException("Error setting up Keycloak context", e);
    }
  }

}
