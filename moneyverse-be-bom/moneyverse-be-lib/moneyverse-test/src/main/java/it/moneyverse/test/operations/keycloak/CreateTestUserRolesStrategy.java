package it.moneyverse.test.operations.keycloak;

import static it.moneyverse.test.operations.keycloak.KeycloakSetupContextConstants.TEST_REALM;

import it.moneyverse.core.enums.UserRoleEnum;
import it.moneyverse.test.model.TestContextModel;
import java.util.Arrays;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RoleRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateTestUserRolesStrategy implements KeycloakConfigurationStrategy {

  private static final Logger LOGGER = LoggerFactory.getLogger(CreateTestUserRolesStrategy.class);

  @Override
  public void configure(Keycloak client, TestContextModel testContext) {
    Arrays.stream(UserRoleEnum.values()).forEach(role -> {
      RoleRepresentation keycloakRole = new RoleRepresentation();
      keycloakRole.setName(role.name());
      keycloakRole.setDescription("Role %s for testing purposes".formatted(role.name()));
      client.realm(TEST_REALM).roles().create(keycloakRole);
      LOGGER.info("KEYCLOAK: Created realm role {}", keycloakRole);
    });
  }
}
