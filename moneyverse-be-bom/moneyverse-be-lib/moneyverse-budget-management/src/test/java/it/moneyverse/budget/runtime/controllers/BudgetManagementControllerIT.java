package it.moneyverse.budget.runtime.controllers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import it.moneyverse.budget.model.dto.BudgetCriteria;
import it.moneyverse.budget.model.dto.BudgetDto;
import it.moneyverse.budget.model.dto.BudgetRequestDto;
import it.moneyverse.budget.model.dto.BudgetUpdateRequestDto;
import it.moneyverse.budget.model.entities.Budget;
import it.moneyverse.budget.model.repositories.BudgetRepository;
import it.moneyverse.budget.utils.BudgetTestContext;
import it.moneyverse.core.enums.UserRoleEnum;
import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.test.annotations.IntegrationTest;
import it.moneyverse.test.extensions.grpc.GrpcMockServer;
import it.moneyverse.test.extensions.testcontainers.KafkaContainer;
import it.moneyverse.test.extensions.testcontainers.KeycloakContainer;
import it.moneyverse.test.extensions.testcontainers.PostgresContainer;
import it.moneyverse.test.utils.AbstractIntegrationTest;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.test.utils.properties.TestPropertyRegistry;
import java.util.List;
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
import org.testcontainers.junit.jupiter.Container;

@IntegrationTest
class BudgetManagementControllerIT extends AbstractIntegrationTest {

  protected static BudgetTestContext testContext;
  @Container static PostgresContainer postgresContainer = new PostgresContainer();
  @Container static KeycloakContainer keycloakContainer = new KeycloakContainer();
  @Container static KafkaContainer kafkaContainer = new KafkaContainer();
  @RegisterExtension static GrpcMockServer mockServer = new GrpcMockServer();
  @Autowired private BudgetRepository budgetRepository;
  private HttpHeaders headers;

  @DynamicPropertySource
  static void mappingProperties(DynamicPropertyRegistry registry) {
    new TestPropertyRegistry(registry)
        .withPostgres(postgresContainer)
        .withKeycloak(keycloakContainer)
        .withGrpcUserService(mockServer.getHost(), mockServer.getPort())
        .withKafkaContainer(kafkaContainer);
  }

  @BeforeAll
  static void beforeAll() {
    testContext =
        new BudgetTestContext().generateScript(tempDir).insertUsersIntoKeycloak(keycloakContainer);
  }

  @BeforeEach
  public void setup() {
    headers = new HttpHeaders();
  }

  @Test
  void testCreateBudget() {
    final String username = testContext.getRandomUser().getUsername();
    headers.setBearerAuth(testContext.getAuthenticationToken(username));
    final BudgetRequestDto request = testContext.createBudgetForUser(username);
    mockServer.mockExistentUser();
    BudgetDto expected = testContext.getExpectedBudgetDto(request);

    ResponseEntity<BudgetDto> response =
        restTemplate.postForEntity(
            basePath + "/budgets", new HttpEntity<>(request, headers), BudgetDto.class);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(testContext.getBudgetsCount() + 1, budgetRepository.findAll().size());
    assertThat(response.getBody())
        .usingRecursiveComparison()
        .ignoringFieldsOfTypes(UUID.class)
        .isEqualTo(expected);
  }

  @Test
  void testGetBudgets() {
    final UserModel user = testContext.getRandomAdminOrUser();
    final BudgetCriteria criteria = testContext.createBudgetCriteria();
    if (user.getRole().equals(UserRoleEnum.USER)) {
      criteria.setUsername(user.getUsername());
    }
    final List<Budget> expected = testContext.filterBudgets(criteria);

    headers.setBearerAuth(testContext.getAuthenticationToken(user.getUsername()));
    ResponseEntity<List<BudgetDto>> response =
        restTemplate.exchange(
            testContext.createUri(basePath + "/budgets", criteria),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<>() {});

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(expected.size(), response.getBody().size());
  }

  @Test
  void testGetBudget() {
    final UserModel user = testContext.getRandomAdminOrUser();
    final UUID budgetId = testContext.getRandomBudget(user.getUsername()).getBudgetId();
    headers.setBearerAuth(testContext.getAuthenticationToken(user.getUsername()));

    ResponseEntity<BudgetDto> response =
        restTemplate.exchange(
            basePath + "/budgets/" + budgetId,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            BudgetDto.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(budgetId, response.getBody().getBudgetId());
  }

  @Test
  void testUpdateBudget() {
    final UserModel user = testContext.getRandomAdminOrUser();
    final UUID budgetId = testContext.getRandomBudget(user.getUsername()).getBudgetId();
    BudgetUpdateRequestDto request =
        new BudgetUpdateRequestDto(
            null, RandomUtils.randomString(25), null, RandomUtils.randomBigDecimal(), null);
    headers.setBearerAuth(testContext.getAuthenticationToken(user.getUsername()));

    ResponseEntity<BudgetDto> response =
        restTemplate.exchange(
            basePath + "/budgets/" + budgetId,
            HttpMethod.PUT,
            new HttpEntity<>(request, headers),
            BudgetDto.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(budgetId, response.getBody().getBudgetId());
    assertEquals(request.description(), response.getBody().getDescription());
    assertEquals(request.budgetLimit(), response.getBody().getBudgetLimit());
  }

  @Test
  void testDeleteAccount() {
    final UserModel user = testContext.getRandomAdminOrUser();
    final UUID budgetId = testContext.getRandomBudget(user.getUsername()).getBudgetId();
    headers.setBearerAuth(testContext.getAuthenticationToken(user.getUsername()));

    ResponseEntity<Void> response =
        restTemplate.exchange(
            basePath + "/budgets/" + budgetId,
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            Void.class);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    assertEquals(testContext.getBudgetsCount() - 1, budgetRepository.count());
  }
}
