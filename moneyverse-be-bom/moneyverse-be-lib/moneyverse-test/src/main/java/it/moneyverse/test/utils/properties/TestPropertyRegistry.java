package it.moneyverse.test.utils.properties;

import static it.moneyverse.test.operations.keycloak.KeycloakSetupContextConstants.TEST_REALM;

import it.moneyverse.core.utils.constants.KeycloakPropertiesConstants;
import it.moneyverse.core.utils.properties.UserServiceGrpcClientProperties;
import it.moneyverse.core.utils.properties.DatasourceProperties;
import it.moneyverse.core.utils.properties.KafkaProperties;
import it.moneyverse.test.extensions.testcontainers.KafkaContainer;
import it.moneyverse.test.extensions.testcontainers.KeycloakContainer;
import it.moneyverse.test.extensions.testcontainers.PostgresContainer;
import org.springframework.test.context.DynamicPropertyRegistry;

public class TestPropertyRegistry {

  private final DynamicPropertyRegistry registry;

  public TestPropertyRegistry(DynamicPropertyRegistry registry) {
    this.registry = registry;
  }

  public TestPropertyRegistry withPostgres(PostgresContainer container) {
    registry.add(DatasourceProperties.DRIVER_CLASS_NAME, container::getDriverClassName);
    registry.add(DatasourceProperties.URL, container::getJdbcUrl);
    registry.add(DatasourceProperties.USERNAME, container::getUsername);
    registry.add(DatasourceProperties.PASSWORD, container::getPassword);
    return this;
  }

  public TestPropertyRegistry withKeycloak(KeycloakContainer container) {
    registry.add(KeycloakPropertiesConstants.KEYCLOAK_HOST, container::getHost);
    registry.add(KeycloakPropertiesConstants.KEYCLOAK_PORT, container::getHttpPort);
    registry.add(KeycloakPropertiesConstants.KEYCLOAK_REALM, () -> TEST_REALM);
    return this;
  }

  public TestPropertyRegistry withGrpcUserService(String host, int port) {
    registry.add(UserServiceGrpcClientProperties.USER_SERVICE_CLIENT_HOST, () -> host);
    registry.add(UserServiceGrpcClientProperties.USER_SERVICE_CLIENT_PORT, () -> port);
    return this;
  }

  public TestPropertyRegistry withKafkaContainer(KafkaContainer container) {
    registry.add(KafkaProperties.BOOTSTRAP_SERVERS, container::getBootstrapServers);
    registry.add(KafkaProperties.GROUP_ID, () -> "test-group");
    return this;
  }
}
