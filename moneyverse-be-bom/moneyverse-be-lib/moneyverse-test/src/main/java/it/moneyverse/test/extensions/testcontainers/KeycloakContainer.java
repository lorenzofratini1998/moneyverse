package it.moneyverse.test.extensions.testcontainers;

import dasniko.testcontainers.keycloak.ExtendableKeycloakContainer;

public class KeycloakContainer extends ExtendableKeycloakContainer<KeycloakContainer> {

  private static final String KEYCLOAK_IMAGE = "quay.io/keycloak/keycloak";
  private static final String KEYCLOAK_VERSION = "26.0";
  private static final String REALM_IMPORT_FILE = "/keycloak/realm-test.json";

  public KeycloakContainer() {
    this(KEYCLOAK_IMAGE + ":" + KEYCLOAK_VERSION);
    super.withRealmImportFile(REALM_IMPORT_FILE);
  }

  public KeycloakContainer(String dockerImageName) {
    super(dockerImageName);
    super.withRealmImportFile(REALM_IMPORT_FILE);
  }
}
