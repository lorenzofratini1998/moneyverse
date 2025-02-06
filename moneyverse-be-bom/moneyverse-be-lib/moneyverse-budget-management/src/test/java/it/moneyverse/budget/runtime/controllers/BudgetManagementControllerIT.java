package it.moneyverse.budget.runtime.controllers;

import static it.moneyverse.budget.utils.BudgetTestUtils.createBudgetRequest;
import static it.moneyverse.budget.utils.BudgetTestUtils.createBudgetUpdateRequest;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import it.moneyverse.budget.model.dto.*;
import it.moneyverse.budget.model.entities.Category;
import it.moneyverse.budget.model.repositories.BudgetRepository;
import it.moneyverse.budget.model.repositories.CategoryRepository;
import it.moneyverse.budget.utils.BudgetCriteriaRandomGenerator;
import it.moneyverse.budget.utils.BudgetTestContext;
import it.moneyverse.core.model.dto.PageCriteria;
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
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.junit.jupiter.Container;

@IntegrationTest
class BudgetManagementControllerIT extends AbstractIntegrationTest {

  protected static BudgetTestContext testContext;
  @Container static PostgresContainer postgresContainer = new PostgresContainer();
  @Container static KeycloakContainer keycloakContainer = new KeycloakContainer();
  @Container static KafkaContainer kafkaContainer = new KafkaContainer();
  @RegisterExtension static GrpcMockServer mockServer = new GrpcMockServer();
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private BudgetRepository budgetRepository;
  private HttpHeaders headers;

  @DynamicPropertySource
  static void mappingProperties(DynamicPropertyRegistry registry) {
    new TestPropertyRegistry(registry)
        .withPostgres(postgresContainer)
        .withKeycloak(keycloakContainer)
        .withGrpcUserService(mockServer.getHost(), mockServer.getPort())
        .withGrpcCurrencyService(mockServer.getHost(), mockServer.getPort())
        .withKafkaContainer(kafkaContainer);
  }

  @BeforeAll
  static void beforeAll() {
    testContext = new BudgetTestContext(keycloakContainer).generateScript(tempDir);
  }

  @BeforeEach
  public void setup() {
    headers = new HttpHeaders();
  }

  @Test
  void testCreateCategory() {
    final UUID userId = testContext.getRandomUser().getUserId();
    headers.setBearerAuth(testContext.getAuthenticationToken(userId));
    final CategoryRequestDto request = testContext.createCategoryForUser(userId);
    mockServer.mockExistentUser();
    CategoryDto expected = testContext.getExpectedCategoryDto(request);

    ResponseEntity<CategoryDto> response =
        restTemplate.postForEntity(
            basePath + "/categories", new HttpEntity<>(request, headers), CategoryDto.class);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(testContext.getCategoriesCount() + 1, categoryRepository.findAll().size());
    assertThat(response.getBody())
        .usingRecursiveComparison()
        .ignoringFieldsOfTypes(UUID.class)
        .isEqualTo(expected);
  }

  @Test
  void testGetCategoriesByUser() {
    final UUID userId = testContext.getRandomUser().getUserId();
    final PageCriteria criteria = new PageCriteria();
    final List<Category> expected = testContext.getCategoriesByUser(userId, criteria);

    headers.setBearerAuth(testContext.getAuthenticationToken(userId));
    ResponseEntity<List<CategoryDto>> response =
        restTemplate.exchange(
            testContext.createUri(basePath + "/categories/users/" + userId, criteria),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<>() {});

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(expected.size(), response.getBody().size());
  }

  @Test
  void testGetCategory() {
    final UserModel user = testContext.getRandomAdminOrUser();
    final UUID categoryId = testContext.getRandomCategoryByUserId(user.getUserId()).getCategoryId();
    headers.setBearerAuth(testContext.getAuthenticationToken(user.getUserId()));

    ResponseEntity<CategoryDto> response =
        restTemplate.exchange(
            basePath + "/categories/" + categoryId,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            CategoryDto.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(categoryId, response.getBody().getCategoryId());
  }

  @Test
  void testUpdateCategory() {
    final UserModel user = testContext.getRandomAdminOrUser();
    final UUID categoryId = testContext.getRandomCategoryByUserId(user.getUserId()).getCategoryId();
    CategoryUpdateRequestDto request =
        new CategoryUpdateRequestDto(null, RandomUtils.randomString(25));
    headers.setBearerAuth(testContext.getAuthenticationToken(user.getUserId()));

    ResponseEntity<CategoryDto> response =
        restTemplate.exchange(
            basePath + "/categories/" + categoryId,
            HttpMethod.PUT,
            new HttpEntity<>(request, headers),
            CategoryDto.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(categoryId, response.getBody().getCategoryId());
    assertEquals(request.description(), response.getBody().getDescription());
  }

  @Test
  void testDeleteCategory() {
    final UserModel user = testContext.getRandomAdminOrUser();
    final UUID categoryId = testContext.getRandomCategoryByUserId(user.getUserId()).getCategoryId();
    headers.setBearerAuth(testContext.getAuthenticationToken(user.getUserId()));

    ResponseEntity<Void> response =
        restTemplate.exchange(
            basePath + "/categories/" + categoryId,
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            Void.class);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    assertEquals(testContext.getCategoriesCount() - 1, categoryRepository.count());
  }

  @Test
  void testCreateBudget() {
    final UUID userId = testContext.getRandomUser().getUserId();
    final Category randomUserCategory = testContext.getRandomCategoryByUserId(userId);
    final BudgetRequestDto request = createBudgetRequest(randomUserCategory.getCategoryId());
    headers.setBearerAuth(testContext.getAuthenticationToken(userId));
    mockServer.mockExistentCurrency();

    ResponseEntity<BudgetDto> response =
        restTemplate.exchange(
            basePath + "/budgets",
            HttpMethod.POST,
            new HttpEntity<>(request, headers),
            BudgetDto.class);

    assertNotNull(response.getBody());
    assertEquals(testContext.getBudgets().size() + 1, budgetRepository.findAll().size());
  }

  @Test
  void testGetBudgetsByUserId() {
    final UUID userId = testContext.getRandomUser().getUserId();
    final BudgetCriteria criteria = new BudgetCriteriaRandomGenerator(testContext).generate();
    headers.setBearerAuth(testContext.getAuthenticationToken(userId));

    ResponseEntity<List<BudgetDto>> response =
        restTemplate.exchange(
            testContext.createUri(basePath + "/budgets/users/" + userId, criteria),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<>() {});

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(testContext.filterBudgets(userId, criteria).size(), response.getBody().size());
  }

  @Test
  void testGetBudget() {
    final UUID userId = testContext.getRandomUser().getUserId();
    final UUID budgetId = testContext.getRandomBudgetByUserId(userId).getBudgetId();
    headers.setBearerAuth(testContext.getAuthenticationToken(userId));

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
    final UUID userId = testContext.getRandomUser().getUserId();
    final UUID budgetId = testContext.getRandomBudgetByUserId(userId).getBudgetId();
    final BudgetUpdateRequestDto request = createBudgetUpdateRequest();
    headers.setBearerAuth(testContext.getAuthenticationToken(userId));
    mockServer.mockExistentCurrency();

    ResponseEntity<BudgetDto> response =
        restTemplate.exchange(
            basePath + "/budgets/" + budgetId,
            HttpMethod.PUT,
            new HttpEntity<>(request, headers),
            BudgetDto.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(budgetId, response.getBody().getBudgetId());
    assertEquals(request.currency(), response.getBody().getCurrency());
    assertEquals(request.amount(), response.getBody().getAmount());
    assertEquals(request.budgetLimit(), response.getBody().getBudgetLimit());
    assertEquals(request.startDate(), response.getBody().getStartDate());
    assertEquals(request.endDate(), response.getBody().getEndDate());
  }

  @Test
  void testDeleteBudget() {
    final UUID userId = testContext.getRandomUser().getUserId();
    final UUID budgetId = testContext.getRandomBudgetByUserId(userId).getBudgetId();
    headers.setBearerAuth(testContext.getAuthenticationToken(userId));

    ResponseEntity<Void> response =
        restTemplate.exchange(
            basePath + "/budgets/" + budgetId,
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            Void.class);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    assertEquals(testContext.getBudgets().size() - 1, budgetRepository.count());
  }

  @Test
  void testGetDefaultCategories() {
    final UUID userId = testContext.getRandomUser().getUserId();
    headers.setBearerAuth(testContext.getAuthenticationToken(userId));

    ResponseEntity<List<CategoryDto>> response =
        restTemplate.exchange(
            UriComponentsBuilder.fromUriString(basePath + "/categories")
                .queryParam("default", true)
                .toUriString(),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<>() {});

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(testContext.getDefaultCategories().size(), response.getBody().size());
  }
}
