package it.moneyverse.test.extensions.testcontainers;

import dasniko.testcontainers.keycloak.ExtendableKeycloakContainer;

public class KeycloakContainer extends ExtendableKeycloakContainer<KeycloakContainer> {

    private static final String KEYCLOAK_IMAGE = "quay.io/keycloak/keycloak";
    private static final String KEYCLOAK_VERSION = "26.0";

    public KeycloakContainer() {
        this(KEYCLOAK_IMAGE + ":" + KEYCLOAK_VERSION);
    }

    public KeycloakContainer(String dockerImageName) {
        super(dockerImageName);
    }

}
