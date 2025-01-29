package it.moneyverse.core.utils.properties;

import java.util.Objects;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = KeycloakAdminProperties.KEYCLOAK_PREFIX)
public class KeycloakAdminProperties {

  public static final String KEYCLOAK_PREFIX = "spring.security.oauth2.keycloak.admin";
  public static final String KEYCLOAK_USERNAME = KEYCLOAK_PREFIX + ".username";
  public static final String KEYCLOAK_PASSWORD = KEYCLOAK_PREFIX + ".password";
  public static final String KEYCLOAK_CLIENT_ID = KEYCLOAK_PREFIX + ".client-id";
  public static final String KEYCLOAK_CLIENT_SECRET = KEYCLOAK_PREFIX + ".client-secret";

  private final String username;
  private final String password;
  private final String clientId;
  private final String clientSecret;

  @ConstructorBinding
  public KeycloakAdminProperties(
      String username, String password, String clientId, String clientSecret) {
    this.username = Objects.requireNonNull(username, KEYCLOAK_USERNAME + " cannot be null");
    this.password = Objects.requireNonNull(password, KEYCLOAK_PASSWORD + " cannot be null");
    this.clientId = Objects.requireNonNull(clientId, KEYCLOAK_CLIENT_ID + " cannot be null");
    this.clientSecret =
        Objects.requireNonNull(clientSecret, KEYCLOAK_CLIENT_SECRET + " cannot be null");
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getClientId() {
    return clientId;
  }

  public String getClientSecret() {
    return clientSecret;
  }
}
