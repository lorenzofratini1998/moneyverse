package it.moneyverse.user.boot;

import it.moneyverse.core.utils.properties.KeycloakAdminProperties;
import it.moneyverse.core.utils.properties.KeycloakProperties;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = {KeycloakAdminProperties.class, KeycloakProperties.class})
public class UserManagementAutoConfiguration {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(UserManagementAutoConfiguration.class);

  private final KeycloakProperties properties;
  private final KeycloakAdminProperties adminProperties;

  public UserManagementAutoConfiguration(
      KeycloakProperties properties, KeycloakAdminProperties adminProperties) {
    this.properties = properties;
    this.adminProperties = adminProperties;
    LOGGER.info("Starting to load beans from {}", UserManagementAutoConfiguration.class);
  }

  @Bean
  Keycloak keycloakClient() {
    return KeycloakBuilder.builder()
        .serverUrl("http://%s:%s".formatted(properties.getHost(), properties.getPort()))
        .realm(properties.getRealmName())
        .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
        .clientId(adminProperties.getClientId())
        .clientSecret(adminProperties.getClientSecret())
        .build();
  }
}
