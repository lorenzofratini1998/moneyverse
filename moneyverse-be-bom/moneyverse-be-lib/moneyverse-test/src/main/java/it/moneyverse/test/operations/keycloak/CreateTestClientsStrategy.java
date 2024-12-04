package it.moneyverse.test.operations.keycloak;

import static it.moneyverse.test.operations.keycloak.KeycloakSetupContextConstants.TEST_REALM;

import it.moneyverse.test.model.TestContextModel;
import jakarta.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.ClientRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateTestClientsStrategy implements KeycloakConfigurationStrategy {

  private static final Logger LOGGER = LoggerFactory.getLogger(CreateTestClientsStrategy.class);
  private final List<String> keycloakClients;

  public CreateTestClientsStrategy(List<String> keycloakClients) {
    this.keycloakClients = keycloakClients;
  }

  @Override
  public void configure(Keycloak client, TestContextModel testContext) {
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
      clientRepresentation.setStandardFlowEnabled(true);
      clientRepresentation.setDirectAccessGrantsEnabled(true);
      saveTestClient(client, clientRepresentation);
    }
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
