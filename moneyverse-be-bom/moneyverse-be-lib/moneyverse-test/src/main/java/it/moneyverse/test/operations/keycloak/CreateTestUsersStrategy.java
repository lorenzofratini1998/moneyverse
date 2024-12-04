package it.moneyverse.test.operations.keycloak;

import static it.moneyverse.test.operations.keycloak.KeycloakSetupContextConstants.TEST_REALM;

import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.test.model.TestContextModel;
import jakarta.ws.rs.core.Response;
import java.util.Collections;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateTestUsersStrategy implements KeycloakConfigurationStrategy {

  private static final Logger LOGGER = LoggerFactory.getLogger(CreateTestUsersStrategy.class);
  private Keycloak keycloak;

  @Override
  public void configure(Keycloak client, TestContextModel testContext) {
    this.keycloak = client;
    createUsers(testContext);
    LOGGER.info("KEYCLOAK: Created {} users ", testContext.getUsers().size());
    addRoleToUsers(testContext);
    LOGGER.info("KEYCLOAK: Added roles to {} users ", testContext.getUsers().size());
  }

  private void createUsers(TestContextModel testContext) {
    testContext.getUsers().stream().map(this::getUserRepresentation).forEach(this::saveTestUser);
  }

  private UserRepresentation getUserRepresentation(UserModel fake) {
    UserRepresentation user = new UserRepresentation();
    user.setId(fake.getUserId().toString());
    user.setUsername(fake.getUsername());
    user.setEmail(fake.getEmail());
    user.setEmailVerified(true);
    user.setFirstName(fake.getName());
    user.setLastName(fake.getSurname());
    user.setEnabled(true);
    CredentialRepresentation credential = new CredentialRepresentation();
    credential.setType(CredentialRepresentation.PASSWORD);
    credential.setTemporary(false);
    credential.setValue(fake.getPassword());
    user.setCredentials(Collections.singletonList(credential));
    return user;
  }

  private void saveTestUser(UserRepresentation user) {
    Response response = keycloak.realm(TEST_REALM).users().create(user);
    if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
      response.close();
      throw new IllegalStateException("Failed to create user: " + user.getUsername());
    }
  }

  private void addRoleToUsers(TestContextModel testContext) {
    RealmResource realm = keycloak.realm(TEST_REALM);
    testContext
        .getUsers()
        .forEach(
            user -> {
              RoleRepresentation realmRole =
                  realm.roles().get(user.getRole().name()).toRepresentation();
              String userId =
                  realm.users().searchByUsername(user.getUsername(), true).getFirst().getId();
              realm
                  .users()
                  .get(userId)
                  .roles()
                  .realmLevel()
                  .add(Collections.singletonList(realmRole));
            });
  }
}
