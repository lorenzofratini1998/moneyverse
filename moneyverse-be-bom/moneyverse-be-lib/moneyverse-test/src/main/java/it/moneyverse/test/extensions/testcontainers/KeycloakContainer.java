package it.moneyverse.test.extensions.testcontainers;

import static it.moneyverse.test.operations.keycloak.KeycloakSetupContextConstants.TEST_FRONTEND_CLIENT;

import dasniko.testcontainers.keycloak.ExtendableKeycloakContainer;
import it.moneyverse.test.model.dto.UserCredential;
import it.moneyverse.test.utils.RandomUtils;
import java.util.List;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.UserRepresentation;

public class KeycloakContainer extends ExtendableKeycloakContainer<KeycloakContainer> {

    private static final String KEYCLOAK_IMAGE = "quay.io/keycloak/keycloak";
    private static final String KEYCLOAK_VERSION = "26.0";

    public KeycloakContainer() {
        this(KEYCLOAK_IMAGE + ":" + KEYCLOAK_VERSION);
    }

    public KeycloakContainer(String dockerImageName) {
        super(dockerImageName);
    }

    public String getTestAuthenticationToken(UserCredential userCredential, String realmName) {
        try (Keycloak keycloakClient = KeycloakBuilder
            .builder()
            .serverUrl(super.getAuthServerUrl())
            .realm(realmName)
            .username(userCredential.username())
            .password(userCredential.password())
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

    public UserRepresentation getRandomUser(String realmName) {
        List<UserRepresentation> users = getKeycloakAdminClient().realm(realmName).users().list();
        return users.get(RandomUtils.randomInteger(0, users.size() - 1));
    }
}
