package it.moneyverse.user.runtime.controllers;

import static org.junit.jupiter.api.Assertions.*;

import it.moneyverse.core.model.dto.PreferenceDto;
import it.moneyverse.core.model.dto.UserDto;
import it.moneyverse.core.model.dto.UserPreferenceDto;
import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.core.utils.properties.KeycloakAdminProperties;
import it.moneyverse.test.annotations.MoneyverseTest;
import it.moneyverse.test.extensions.grpc.GrpcMockServer;
import it.moneyverse.test.extensions.testcontainers.KafkaContainer;
import it.moneyverse.test.extensions.testcontainers.KeycloakContainer;
import it.moneyverse.test.extensions.testcontainers.PostgresContainer;
import it.moneyverse.test.operations.keycloak.KeycloakSetupContextConstants;
import it.moneyverse.test.utils.AbstractIntegrationTest;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.test.utils.properties.TestPropertyRegistry;
import it.moneyverse.user.model.PreferenceTestFactory;
import it.moneyverse.user.model.UserTestContext;
import it.moneyverse.user.model.UserTestFactory;
import it.moneyverse.user.model.dto.*;
import it.moneyverse.user.model.entities.Preference;
import it.moneyverse.user.model.entities.UserPreference;
import it.moneyverse.user.model.repositories.PreferenceRepository;
import it.moneyverse.user.model.repositories.UserPreferenceRepository;
import it.moneyverse.user.services.KeycloakService;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.junit.jupiter.Container;

@MoneyverseTest
class UserManagementControllerIT extends AbstractIntegrationTest {

  protected static UserTestContext testContext;
  @Container static PostgresContainer postgresContainer = new PostgresContainer();
  @Container static KeycloakContainer keycloakContainer = new KeycloakContainer();
  @Container static KafkaContainer kafkaContainer = new KafkaContainer();
  @RegisterExtension static GrpcMockServer mockServer = new GrpcMockServer();
  @Autowired private UserPreferenceRepository userPreferenceRepository;
  @Autowired private PreferenceRepository preferenceRepository;
  @Autowired private KeycloakService keycloakService;
  private HttpHeaders headers;

  @DynamicPropertySource
  static void mappingProperties(DynamicPropertyRegistry registry) {
    UserModel admin = testContext.getAdminUser();
    new TestPropertyRegistry(registry)
        .withPostgres(postgresContainer)
        .withKeycloak(keycloakContainer)
        .withGrpcCurrencyService(mockServer.getHost(), mockServer.getPort())
        .withKafkaContainer(kafkaContainer)
        .withFlywayTestDirectory(tempDir);
    registry.add(
        KeycloakAdminProperties.KEYCLOAK_CLIENT_ID,
        () -> KeycloakSetupContextConstants.TEST_CLIENT);
    registry.add(
        KeycloakAdminProperties.KEYCLOAK_CLIENT_SECRET,
        () -> KeycloakSetupContextConstants.TEST_CLIENT);
  }

  @BeforeAll
  static void beforeAll() {
    testContext = new UserTestContext(keycloakContainer).generateScript(tempDir);
  }

  @BeforeEach
  public void setup() {
    headers = new HttpHeaders();
  }

  @Test
  void testCreateUserPreferences() {
    Preference preference = PreferenceTestFactory.fakePreference();
    preference.setPreferenceId(null);
    preference = preferenceRepository.save(preference);
    final UserModel userModel = testContext.getRandomUser();
    UUID userId = userModel.getUserId();
    headers.setBearerAuth(testContext.getAuthenticationToken(userId));
    final List<UserPreferenceRequest> request =
        Collections.singletonList(
            PreferenceTestFactory.UserPreferenceRequestBuilder.builder()
                .withPreferenceId(preference.getPreferenceId())
                .build());
    mockServer.mockExistentCurrency(RandomUtils.randomCurrency());
    int initialSize = userPreferenceRepository.findAll().size();

    ResponseEntity<List<UserPreferenceDto>> response =
        restTemplate.exchange(
            basePath + "/users/%s/preferences".formatted(userId),
            HttpMethod.POST,
            new HttpEntity<>(request, headers),
            new ParameterizedTypeReference<>() {});

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(
        initialSize + response.getBody().size(), userPreferenceRepository.findAll().size());
    assertEquals(userId, response.getBody().getFirst().getUserId());
    assertEquals(
        request.getFirst().preferenceId(),
        response.getBody().getFirst().getPreference().getPreferenceId());
    assertEquals(request.getFirst().value(), response.getBody().getFirst().getValue());
  }

  @Test
  void testGetUserPreferences() {
    final UserModel userModel = testContext.getRandomUser();
    UUID userId = userModel.getUserId();
    headers.setBearerAuth(testContext.getAuthenticationToken(userId));
    final boolean mandatory = RandomUtils.flipCoin();
    List<UserPreference> expected =
        mandatory
            ? testContext.getMandatoryUserPreferencesByUserId(userId)
            : testContext.getUserPreferencesByUserId(userId);
    String path = basePath + "/users/%s/preferences".formatted(userId);

    ResponseEntity<List<UserPreferenceDto>> response =
        restTemplate.exchange(
            mandatory
                ? UriComponentsBuilder.fromUriString(path)
                    .queryParam("mandatory", true)
                    .toUriString()
                : path,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<>() {});

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(expected.size(), response.getBody().size());
  }

  @Test
  void testGetUserPreference() {
    final UserModel userModel = testContext.getRandomUser();
    UUID userId = userModel.getUserId();
    headers.setBearerAuth(testContext.getAuthenticationToken(userId));
    final UserPreference userPreference = testContext.getRandomUserPreference(userId);
    ResponseEntity<UserPreferenceDto> response =
        restTemplate.exchange(
            basePath
                + "/users/%s/preferences/%s"
                    .formatted(userId, userPreference.getPreference().getName()),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            UserPreferenceDto.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(userId, response.getBody().getUserId());
    assertEquals(
        userPreference.getPreference().getName(), response.getBody().getPreference().getName());
    assertEquals(userPreference.getValue(), response.getBody().getValue());
  }

  @Test
  void testGetPreferences() {
    final UserModel userModel = testContext.getRandomUser();
    headers.setBearerAuth(testContext.getAuthenticationToken(userModel.getUserId()));
    final boolean mandatory = RandomUtils.flipCoin();
    List<Preference> expected =
        mandatory ? testContext.getMandatoryPreferences() : testContext.getPreferences();
    String path = basePath + "/preferences";

    ResponseEntity<List<PreferenceDto>> response =
        restTemplate.exchange(
            mandatory
                ? UriComponentsBuilder.fromUriString(path)
                    .queryParam("mandatory", true)
                    .toUriString()
                : path,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<>() {});

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(expected.size(), response.getBody().size());
  }

  @Test
  void testGetUser() {
    final UserModel userModel = testContext.getRandomUser();
    headers.setBearerAuth(testContext.getAuthenticationToken(userModel.getUserId()));

    ResponseEntity<UserDto> response =
        restTemplate.exchange(
            basePath + "/users/%s".formatted(userModel.getUserId()),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            UserDto.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(userModel.getUserId(), response.getBody().getUserId());
    assertEquals(userModel.getName(), response.getBody().getFirstName());
    assertEquals(userModel.getSurname(), response.getBody().getLastName());
    assertEquals(userModel.getEmail(), response.getBody().getEmail());
  }

  @Test
  void testUpdateUser() {
    final UserModel userModel = testContext.getRandomUser();
    headers.setBearerAuth(testContext.getAuthenticationToken(userModel.getUserId()));
    UserUpdateRequestDto request =
        UserTestFactory.UserUpdateRequestBuilder.builder().withNullEmail().build();

    ResponseEntity<UserDto> response =
        restTemplate.exchange(
            basePath + "/users/%s".formatted(userModel.getUserId()),
            HttpMethod.PUT,
            new HttpEntity<>(request, headers),
            UserDto.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(userModel.getUserId(), response.getBody().getUserId());
    assertEquals(request.firstName(), response.getBody().getFirstName());
    assertEquals(request.lastName(), response.getBody().getLastName());
    assertEquals(userModel.getEmail(), response.getBody().getEmail());
  }

  @Test
  void testDisableUser() {
    final UserModel userModel = testContext.getRandomUser();
    headers.setBearerAuth(testContext.getAuthenticationToken(userModel.getUserId()));

    ResponseEntity<Void> response =
        restTemplate.exchange(
            basePath + "/users/%s/disable".formatted(userModel.getUserId()),
            HttpMethod.PATCH,
            new HttpEntity<>(headers),
            Void.class);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }

  @Test
  void testDeleteUser() {
    final UserModel userModel = testContext.getRandomUser();
    headers.setBearerAuth(testContext.getAuthenticationToken(userModel.getUserId()));

    ResponseEntity<Void> response =
        restTemplate.exchange(
            basePath + "/users/%s".formatted(userModel.getUserId()),
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            Void.class);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    assertEquals(Optional.empty(), keycloakService.getUserById(userModel.getUserId()));
  }
}
