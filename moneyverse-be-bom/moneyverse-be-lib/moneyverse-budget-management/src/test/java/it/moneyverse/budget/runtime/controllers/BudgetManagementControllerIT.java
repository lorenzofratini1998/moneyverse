package it.moneyverse.budget.runtime.controllers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import it.moneyverse.budget.model.dto.BudgetDto;
import it.moneyverse.budget.model.dto.BudgetCriteria;
import it.moneyverse.budget.model.dto.BudgetRequestDto;
import it.moneyverse.budget.model.entities.Budget;
import it.moneyverse.budget.model.repositories.BudgetRepository;
import it.moneyverse.budget.utils.BudgetTestContext;
import it.moneyverse.core.model.entities.BudgetModel;
import it.moneyverse.test.annotations.IntegrationTest;
import it.moneyverse.test.enums.TestModelStrategyEnum;
import it.moneyverse.test.extensions.grpc.GrpcMockUserService;
import it.moneyverse.test.extensions.testcontainers.KeycloakContainer;
import it.moneyverse.test.extensions.testcontainers.PostgresContainer;
import it.moneyverse.test.model.dto.ScriptMetadata;
import it.moneyverse.test.utils.AbstractIntegrationTest;
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
  @RegisterExtension static GrpcMockUserService mockUserService = new GrpcMockUserService();
  @Autowired private BudgetRepository budgetRepository;
  private HttpHeaders headers;

  @DynamicPropertySource
  static void mappingProperties(DynamicPropertyRegistry registry) {
    new TestPropertyRegistry(registry)
        .withPostgres(postgresContainer)
        .withKeycloak(keycloakContainer)
        .withGrpcUserService(mockUserService.getHost(), mockUserService.getPort());
  }

  @BeforeAll
  static void beforeAll() {
    testContext =
        BudgetTestContext.builder()
            .withStrategy(TestModelStrategyEnum.RANDOM)
            .withTestUsers()
            .withTestBudgets()
            .withKeycloak(keycloakContainer)
            .withScriptMetadata(new ScriptMetadata(tempDir, Budget.class))
            .build();
  }

  @BeforeEach
  public void setup() {
    headers = new HttpHeaders();
  }

  @Test
  void testCreateBudget_Success() {
    final String username = testContext.getRandomUser().getUsername();
    headers.setBearerAuth(testContext.getAuthenticationToken(username));
    final BudgetRequestDto request = testContext.createBudgetForUser(username);
    mockUserService.mockExistentUser();
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
  void testGetBudgetsAdminRole_Success() {
    final String admin = testContext.getAdminUser().getUsername();
    final BudgetCriteria criteria = testContext.createBudgetCriteria();
    final List<BudgetModel> expected = testContext.filterBudgets(criteria);

    ResponseEntity<List<BudgetDto>> response = testGetBudgets(admin, criteria);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(expected.size(), response.getBody().size());
  }

  @Test
  void testGetBudgetsUserRole_Success() {
    final String user = testContext.getRandomUser().getUsername();
    final BudgetCriteria criteria = testContext.createBudgetCriteria();
    criteria.setUsername(user);
    final List<BudgetModel> expected = testContext.filterBudgets(criteria);

    ResponseEntity<List<BudgetDto>> response = testGetBudgets(user, criteria);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(expected.size(), response.getBody().size());
  }

  @Test
  void testGetBudgetsUserRole_Unauthorized() {
    final String user = testContext.getRandomUser().getUsername();
    final BudgetCriteria criteria = testContext.createBudgetCriteria();
    criteria.setUsername(null);

    ResponseEntity<List<BudgetDto>> response = testGetBudgets(user, criteria);

    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
  }

  private ResponseEntity<List<BudgetDto>> testGetBudgets(String username, BudgetCriteria criteria) {
    headers.setBearerAuth(testContext.getAuthenticationToken(username));
    return restTemplate.exchange(
            testContext.createUri(basePath + "/budgets", criteria),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<>() {}
    );
  }
}
