package it.moneyverse.test.extensions.testcontainers;

import static it.moneyverse.test.operations.keycloak.KeycloakSetupContextConstants.TEST_FRONTEND_CLIENT;

import dasniko.testcontainers.keycloak.ExtendableKeycloakContainer;
import it.moneyverse.core.model.entities.UserModel;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;

public class KeycloakContainer extends ExtendableKeycloakContainer<KeycloakContainer> {

    private static final String KEYCLOAK_IMAGE = "quay.io/keycloak/keycloak";
    private static final String KEYCLOAK_VERSION = "26.0";

    public KeycloakContainer() {
        this(KEYCLOAK_IMAGE + ":" + KEYCLOAK_VERSION);
    }

    public KeycloakContainer(String dockerImageName) {
        super(dockerImageName);
    }

    public String getTestAuthenticationToken(UserModel user, String realmName) {
        try (Keycloak keycloakClient = KeycloakBuilder
            .builder()
            .serverUrl(super.getAuthServerUrl())
            .realm(realmName)
            .username(user.getUsername())
            .password(user.getPassword())
            .grantType(OAuth2Constants.PASSWORD)
            .clientId(TEST_FRONTEND_CLIENT)
            .clientSecret(TEST_FRONTEND_CLIENT)
            .build()
        ) {
            return keycloakClient.tokenManager().getAccessTokenString();
        } catch (Exception e) {
            throw new IllegalStateException("Can't get access token", e);
        }
    }

}
