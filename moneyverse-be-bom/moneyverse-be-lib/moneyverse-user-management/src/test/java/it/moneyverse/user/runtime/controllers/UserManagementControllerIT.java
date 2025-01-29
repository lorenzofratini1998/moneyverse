package it.moneyverse.user.runtime.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.core.utils.properties.KeycloakAdminProperties;
import it.moneyverse.test.annotations.IntegrationTest;
import it.moneyverse.test.extensions.grpc.GrpcMockServer;
import it.moneyverse.test.extensions.testcontainers.KafkaContainer;
import it.moneyverse.test.extensions.testcontainers.KeycloakContainer;
import it.moneyverse.test.extensions.testcontainers.PostgresContainer;
import it.moneyverse.test.operations.keycloak.KeycloakSetupContextConstants;
import it.moneyverse.test.utils.AbstractIntegrationTest;
import it.moneyverse.test.utils.properties.TestPropertyRegistry;
import it.moneyverse.user.enums.PreferenceKeyEnum;
import it.moneyverse.user.model.dto.PreferenceDto;
import it.moneyverse.user.model.dto.PreferenceRequest;
import it.moneyverse.user.model.repositories.PreferenceRepository;
import it.moneyverse.user.utils.UserTestContext;
import it.moneyverse.user.utils.UserTestUtils;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;

@IntegrationTest
public class UserManagementControllerIT extends AbstractIntegrationTest {

  protected static UserTestContext testContext;
  @Container static PostgresContainer postgresContainer = new PostgresContainer();
  @Container static KeycloakContainer keycloakContainer = new KeycloakContainer();
  @Container static KafkaContainer kafkaContainer = new KafkaContainer();
  @RegisterExtension static GrpcMockServer mockServer = new GrpcMockServer();
  @Autowired private PreferenceRepository preferenceRepository;
  private HttpHeaders headers;

  @DynamicPropertySource
  static void mappingProperties(DynamicPropertyRegistry registry) {
    UserModel admin = testContext.getAdminUser();
    new TestPropertyRegistry(registry)
        .withPostgres(postgresContainer)
        .withKeycloak(keycloakContainer)
        .withGrpcCurrencyService(mockServer.getHost(), mockServer.getPort())
        .withKafkaContainer(kafkaContainer);
    registry.add(KeycloakAdminProperties.KEYCLOAK_USERNAME, admin::getUsername);
    registry.add(KeycloakAdminProperties.KEYCLOAK_PASSWORD, admin::getPassword);
    registry.add(
        KeycloakAdminProperties.KEYCLOAK_CLIENT_ID,
        () -> KeycloakSetupContextConstants.TEST_CLIENT);
    registry.add(
        KeycloakAdminProperties.KEYCLOAK_CLIENT_SECRET,
        () -> KeycloakSetupContextConstants.TEST_CLIENT);
  }

  @BeforeAll
  static void beforeAll() {
    testContext =
        new UserTestContext().generateScript(tempDir).insertUsersIntoKeycloak(keycloakContainer);
  }

  @BeforeEach
  public void setup() {
    headers = new HttpHeaders();
  }

  @Test
  public void testCreatePreferences() {
    final UserModel userModel = testContext.getRandomUser();
    UUID userId = userModel.getUserId();
    headers.setBearerAuth(testContext.getAuthenticationToken(userModel.getUsername()));
    final List<PreferenceRequest> request = UserTestUtils.createPreferencesRequest();
    mockServer.mockExistentCurrency();

    ResponseEntity<PreferenceDto> response =
        restTemplate.postForEntity(
            basePath + "/users/%s/preferences".formatted(userId),
            new HttpEntity<>(request, headers),
            PreferenceDto.class);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(PreferenceKeyEnum.values().length, response.getBody().getPreferences().size());
  }
}
