package it.moneyverse.test.utils.properties;

import static it.moneyverse.test.operations.keycloak.KeycloakSetupContextConstants.TEST_REALM;

import it.moneyverse.core.utils.properties.*;
import it.moneyverse.test.extensions.testcontainers.*;
import it.moneyverse.test.utils.RandomUtils;
import java.nio.file.Path;
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

  public TestPropertyRegistry withRedis(RedisContainer container) {
    registry.add(RedisProperties.HOST, container::getRedisHost);
    registry.add(RedisProperties.PORT, container::getRedisPort);
    registry.add(RedisProperties.PASSWORD, container::getPassword);
    return this;
  }

  public TestPropertyRegistry withKeycloak(KeycloakContainer container) {
    registry.add(KeycloakProperties.KEYCLOAK_HOST, container::getHost);
    registry.add(KeycloakProperties.KEYCLOAK_PORT, container::getHttpPort);
    registry.add(KeycloakProperties.KEYCLOAK_REALM, () -> TEST_REALM);
    return this;
  }

  public TestPropertyRegistry withGrpcUserService(String host, int port) {
    registry.add(UserServiceGrpcClientProperties.USER_SERVICE_CLIENT_HOST, () -> host);
    registry.add(UserServiceGrpcClientProperties.USER_SERVICE_CLIENT_PORT, () -> port);
    return this;
  }

  public TestPropertyRegistry withGrpcAccountService(String host, int port) {
    registry.add(AccountServiceGrpcClientProperties.ACCOUNT_SERVICE_CLIENT_HOST, () -> host);
    registry.add(AccountServiceGrpcClientProperties.ACCOUNT_SERVICE_CLIENT_PORT, () -> port);
    return this;
  }

  public TestPropertyRegistry withGrpcBudgetService(String host, int port) {
    registry.add(BudgetServiceGrpcClientProperties.BUDGET_SERVICE_CLIENT_HOST, () -> host);
    registry.add(BudgetServiceGrpcClientProperties.BUDGET_SERVICE_CLIENT_PORT, () -> port);
    return this;
  }

  public TestPropertyRegistry withGrpcCurrencyService(String host, int port) {
    registry.add(CurrencyServiceGrpcClientProperties.CURRENCY_SERVICE_CLIENT_HOST, () -> host);
    registry.add(CurrencyServiceGrpcClientProperties.CURRENCY_SERVICE_CLIENT_PORT, () -> port);
    return this;
  }

  public TestPropertyRegistry withKafkaContainer(KafkaContainer container) {
    registry.add(
        KafkaProperties.KafkaAdminProperties.BOOTSTRAP_SERVERS, container::getBootstrapServers);
    registry.add(
        KafkaProperties.KafkaConsumerProperties.GROUP_ID,
        () -> RandomUtils.randomUUID().toString());
    return this;
  }

  public TestPropertyRegistry withFlywayTestDirectory(Path tempDir) {
    registry.add(
        "spring.flyway.locations",
        () ->
            "classpath:db/migration/common/schema,filesystem:%s"
                .formatted(tempDir.toAbsolutePath().toString()));
    return this;
  }

  public TestPropertyRegistry withEmbeddedKafka() {
    registry.add(
        KafkaProperties.KafkaConsumerProperties.GROUP_ID,
        () -> RandomUtils.randomUUID().toString());
    return this;
  }

  public TestPropertyRegistry withClickhouse(ClickhouseContainer container) {
    registry.add(ClickhouseProperties.DRIVER_CLASS_NAME, container::getDriverClassName);
    registry.add(ClickhouseProperties.URL, container::getJdbcUrl);
    registry.add(ClickhouseProperties.USERNAME, container::getUsername);
    registry.add(ClickhouseProperties.PASSWORD, container::getPassword);
    return this;
  }
}
