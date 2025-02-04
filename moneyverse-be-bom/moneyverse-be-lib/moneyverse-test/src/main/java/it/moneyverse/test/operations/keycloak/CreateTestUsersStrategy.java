package it.moneyverse.test.operations.keycloak;

import static it.moneyverse.test.operations.keycloak.KeycloakSetupContextConstants.TEST_REALM;

import it.moneyverse.core.model.entities.UserModel;
import jakarta.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;
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
  public void configure(Keycloak client, List<UserModel> users) {
    this.keycloak = client;
    createUsers(users);
    LOGGER.info("KEYCLOAK: Created {} users ", users.size());
    addRoleToUsers(users);
    LOGGER.info("KEYCLOAK: Added roles to {} users ", users.size());
  }

  private void createUsers(List<UserModel> users) {
    users.stream().map(this::getUserRepresentation).forEach(this::saveTestUser);
    RealmResource realm = keycloak.realm(TEST_REALM);
    users.forEach(
        user -> {
          Optional.ofNullable(realm.users().searchByUsername(user.getUsername(), true).getFirst())
              .map(UserRepresentation::getId)
              .map(UUID::fromString)
              .ifPresent(user::setUserId);
        });
  }

  private UserRepresentation getUserRepresentation(UserModel fake) {
    UserRepresentation user = new UserRepresentation();
    user.setUsername(fake.getUsername());
    user.setEmail(fake.getEmail());
    user.setEmailVerified(true);
    user.setFirstName(fake.getName());
    user.setLastName(fake.getSurname());
    user.setEnabled(true);
    user.setAttributes(
        fake.getAttributes().entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> List.of(entry.getValue()))));
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

  private void addRoleToUsers(List<UserModel> users) {
    RealmResource realm = keycloak.realm(TEST_REALM);
    users.forEach(
        user -> {
          RoleRepresentation realmRole =
              realm.roles().get(user.getRole().name()).toRepresentation();
          realm
              .users()
              .get(user.getUserId().toString())
              .roles()
              .realmLevel()
              .add(Collections.singletonList(realmRole));
        });
  }
}
