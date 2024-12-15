package it.moneyverse.test.operations.keycloak;

import static it.moneyverse.test.operations.keycloak.KeycloakSetupContextConstants.TEST_REALM;

import it.moneyverse.core.enums.UserRoleEnum;
import it.moneyverse.test.model.TestContextModel;
import java.util.Arrays;
import java.util.List;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateTestUserRolesStrategy implements KeycloakConfigurationStrategy {

  private static final Logger LOGGER = LoggerFactory.getLogger(CreateTestUserRolesStrategy.class);

  @Override
  public void configure(Keycloak client, TestContextModel testContext) {
    Arrays.stream(UserRoleEnum.values())
        .forEach(
            role -> {
              RoleRepresentation keycloakRole = new RoleRepresentation();
              keycloakRole.setName(role.name());
              keycloakRole.setDescription("Role %s for testing purposes".formatted(role.name()));
              client.realm(TEST_REALM).roles().create(keycloakRole);
              LOGGER.info("KEYCLOAK: Created realm role {}", keycloakRole);
            });
    enhanceAdminRole(client);
  }

  private void enhanceAdminRole(Keycloak client) {
    RoleResource adminRole = client.realm(TEST_REALM).roles().get(UserRoleEnum.ADMIN.name());
    adminRole.addComposites(getRolesByClientId(client, "realm-management"));
    adminRole.addComposites(getRolesByClientId(client, "account"));
  }

  private List<RoleRepresentation> getRolesByClientId(Keycloak keycloak, String clientId) {
    RealmResource realmResource = keycloak.realm(TEST_REALM);
    String id = realmResource.clients().findByClientId(clientId).getFirst().getId();
    return realmResource.clients().get(id).roles().list();
  }
}
