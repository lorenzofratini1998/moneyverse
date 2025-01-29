package it.moneyverse.test.operations.keycloak;

import static it.moneyverse.test.operations.keycloak.KeycloakSetupContextConstants.TEST_REALM;

import it.moneyverse.core.model.entities.UserModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RealmRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateTestRealmStrategy implements KeycloakConfigurationStrategy {

  private static final Logger LOGGER = LoggerFactory.getLogger(CreateTestRealmStrategy.class);

  @Override
  public void configure(Keycloak client, List<UserModel> users) {
    RealmRepresentation realm = new RealmRepresentation();
    realm.setRealm(TEST_REALM);
    realm.setEnabled(true);
    Map<String, String> attributes = new HashMap<>();
    attributes.put("unmanagedAttributesEnabled", "true"); // Set your custom flag

    // Set attributes to the realm
    realm.setAttributes(attributes);
    client.realms().create(realm);
    LOGGER.info("KEYCLOAK: Created test realm {}", TEST_REALM);
  }
}
