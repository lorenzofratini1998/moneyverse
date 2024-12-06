package it.moneyverse.test.utils.properties;

import static it.moneyverse.test.operations.keycloak.KeycloakSetupContextConstants.TEST_REALM;

import it.moneyverse.core.utils.properties.DatasourceProperties;
import it.moneyverse.core.utils.constants.KeycloakPropertiesConstants;
import it.moneyverse.test.extensions.testcontainers.KeycloakContainer;
import it.moneyverse.test.extensions.testcontainers.PostgresContainer;
import org.springframework.test.context.DynamicPropertyRegistry;

public class TestPropertiesHelper {

  public static void setupPostgresProperties(
      DynamicPropertyRegistry registry, PostgresContainer container) {
    registry.add(DatasourceProperties.DRIVER_CLASS_NAME, container::getDriverClassName);
    registry.add(DatasourceProperties.URL, container::getJdbcUrl);
    registry.add(DatasourceProperties.USERNAME, container::getUsername);
    registry.add(DatasourceProperties.PASSWORD, container::getPassword);
  }

  public static void setupKeycloakProperties(
      DynamicPropertyRegistry registry, KeycloakContainer container) {
    registry.add(KeycloakPropertiesConstants.KEYCLOAK_HOST, container::getHost);
    registry.add(KeycloakPropertiesConstants.KEYCLOAK_PORT, container::getHttpPort);
    registry.add(KeycloakPropertiesConstants.KEYCLOAK_REALM, () -> TEST_REALM);
  }
}
