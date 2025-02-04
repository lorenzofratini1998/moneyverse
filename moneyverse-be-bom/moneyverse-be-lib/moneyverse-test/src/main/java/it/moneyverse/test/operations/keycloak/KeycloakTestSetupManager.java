package it.moneyverse.test.operations.keycloak;

import static it.moneyverse.test.operations.keycloak.KeycloakSetupContextConstants.TEST_CLIENT;
import static it.moneyverse.test.operations.keycloak.KeycloakSetupContextConstants.TEST_REALM;

import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.test.extensions.testcontainers.KeycloakContainer;
import it.moneyverse.test.model.dto.UserCredential;
import java.util.List;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;

public class KeycloakTestSetupManager {

  private final KeycloakContainer keycloakContainer;
  private final KeycloakTestSetupContext keycloakContext;
  private final List<String> keycloakClients = List.of(TEST_CLIENT);
  private final List<UserModel> users;

  public KeycloakTestSetupManager(KeycloakContainer keycloakContainer, List<UserModel> users) {
    this.keycloakContainer = keycloakContainer;
    this.users = users;
    this.keycloakContext =
        new KeycloakTestSetupContext()
            .addStrategy(new CreateTestUserRolesStrategy())
            .addStrategy(new CreateTestUsersStrategy())
            .addStrategy(new CreateTestClientsStrategy(keycloakClients));
  }

  public void setup() {
    try {
      Keycloak keycloakAdminClient = keycloakContainer.getKeycloakAdminClient();
      keycloakContext.execute(keycloakAdminClient, users);
    } catch (Exception e) {
      throw new RuntimeException("Error setting up Keycloak context", e);
    }
  }

  public String getTestAuthenticationToken(UserCredential userCredential) {
    try (Keycloak keycloakClient =
        KeycloakBuilder.builder()
            .serverUrl(keycloakContainer.getAuthServerUrl())
            .realm(TEST_REALM)
            .username(userCredential.username())
            .password(userCredential.password())
            .grantType(OAuth2Constants.PASSWORD)
            .clientId(TEST_CLIENT)
            .clientSecret(TEST_CLIENT)
            .build()) {
      return keycloakClient.tokenManager().getAccessTokenString();
    } catch (Exception e) {
      throw new IllegalStateException("Can't get access token", e);
    }
  }
}
