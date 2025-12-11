package it.moneyverse.test.operations.keycloak;

import static it.moneyverse.test.operations.keycloak.KeycloakSetupContextConstants.TEST_REALM;

import it.moneyverse.core.enums.UserRoleEnum;
import it.moneyverse.core.model.entities.UserModel;
import jakarta.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateTestClientsStrategy implements KeycloakConfigurationStrategy {

  private static final Logger LOGGER = LoggerFactory.getLogger(CreateTestClientsStrategy.class);
  private final List<String> keycloakClients;

  public CreateTestClientsStrategy(List<String> keycloakClients) {
    this.keycloakClients = keycloakClients;
  }

  @Override
  public void configure(Keycloak client, List<UserModel> users) {
    for (String keycloakClient : keycloakClients) {
      ClientRepresentation clientRepresentation = new ClientRepresentation();
      clientRepresentation.setClientId(keycloakClient);
      clientRepresentation.setSecret(keycloakClient);
      clientRepresentation.setName(
          Arrays.stream(keycloakClient.split("-"))
                  .map(String::toUpperCase)
                  .collect(Collectors.joining(" "))
              + " CLIENT");
      clientRepresentation.setEnabled(true);
      clientRepresentation.setServiceAccountsEnabled(true);
      clientRepresentation.setDirectAccessGrantsEnabled(true);
      saveTestClient(client, clientRepresentation);
      assignServiceAccountRoles(client, keycloakClient);
    }
  }

  private void assignServiceAccountRoles(Keycloak client, String clientId) {
    RealmResource realmResource = client.realm(TEST_REALM);

    ClientRepresentation createdClient =
        realmResource.clients().findByClientId(clientId).getFirst();

    UserRepresentation serviceAccountUser =
        realmResource.clients().get(createdClient.getId()).getServiceAccountUser();

    if (serviceAccountUser == null) {
      throw new RuntimeException("Service account user not found for client: " + clientId);
    }

    RoleRepresentation realmRole =
        realmResource.roles().get(UserRoleEnum.ADMIN.name()).toRepresentation();

    realmResource
        .users()
        .get(serviceAccountUser.getId())
        .roles()
        .realmLevel()
        .add(Collections.singletonList(realmRole));
  }

  private void saveTestClient(Keycloak keycloak, ClientRepresentation client) {
    Response response = keycloak.realm(TEST_REALM).clients().create(client);
    if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
      response.close();
      throw new IllegalStateException("Failed to create client: " + client.getName());
    }
    LOGGER.info("KEYCLOAK: Created client: {}", client.getName());
  }
}
