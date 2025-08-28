package it.moneyverse.budget.runtime.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import it.moneyverse.budget.model.BudgetTestContext;
import it.moneyverse.budget.model.BudgetTestFactory;
import it.moneyverse.budget.model.CategoryTestFactory;
import it.moneyverse.budget.model.dto.*;
import it.moneyverse.budget.model.entities.Category;
import it.moneyverse.budget.model.repositories.BudgetRepository;
import it.moneyverse.budget.model.repositories.CategoryRepository;
import it.moneyverse.core.model.dto.BudgetDto;
import it.moneyverse.core.model.dto.CategoryDto;
import it.moneyverse.core.model.dto.PageCriteria;
import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.test.annotations.MoneyverseTest;
import it.moneyverse.test.extensions.grpc.GrpcMockServer;
import it.moneyverse.test.extensions.testcontainers.KafkaContainer;
import it.moneyverse.test.extensions.testcontainers.KeycloakContainer;
import it.moneyverse.test.extensions.testcontainers.PostgresContainer;
import it.moneyverse.test.extensions.testcontainers.RedisContainer;
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
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.junit.jupiter.Container;

@MoneyverseTest
class BudgetManagementControllerIT extends AbstractIntegrationTest {

  protected static BudgetTestContext testContext;
  @Container static PostgresContainer postgresContainer = new PostgresContainer();
  @Container static KeycloakContainer keycloakContainer = new KeycloakContainer();
  @Container static KafkaContainer kafkaContainer = new KafkaContainer();
  @Container static RedisContainer redisContainer = new RedisContainer();
  @RegisterExtension static GrpcMockServer mockServer = new GrpcMockServer();
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private BudgetRepository budgetRepository;
  private HttpHeaders headers;

  @DynamicPropertySource
  static void mappingProperties(DynamicPropertyRegistry registry) {
    new TestPropertyRegistry(registry)
        .withPostgres(postgresContainer)
        .withRedis(redisContainer)
        .withKeycloak(keycloakContainer)
        .withGrpcUserService(mockServer.getHost(), mockServer.getPort())
        .withGrpcCurrencyService(mockServer.getHost(), mockServer.getPort())
        .withKafkaContainer(kafkaContainer)
        .withFlywayTestDirectory(tempDir);
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

    ResponseEntity<CategoryDto> response =
        restTemplate.postForEntity(
            basePath + "/categories", new HttpEntity<>(request, headers), CategoryDto.class);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(testContext.getCategoriesCount() + 1, categoryRepository.findAll().size());
    assertNotNull(response.getBody());
    assertEquals(request.userId(), response.getBody().getUserId());
    assertEquals(request.description(), response.getBody().getDescription());
    assertEquals(request.categoryName(), response.getBody().getCategoryName());
    if (response.getBody().getParentCategory() != null) {
      assertEquals(request.parentId(), response.getBody().getParentCategory());
    }
  }

  @Test
  void testGetCategoriesByUser() {
    final UUID userId = testContext.getRandomUser().getUserId();
    final PageCriteria criteria = new PageCriteria();
    criteria.setOffset(0);
    criteria.setLimit(Integer.MAX_VALUE);
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
  void testCreateUserDefaultCategories() {
    final UUID userId = testContext.getRandomUser().getUserId();
    headers.setBearerAuth(testContext.getAuthenticationToken(userId));

    ResponseEntity<Void> response =
        restTemplate.exchange(
            basePath + "/categories/users/" + userId + "/default",
            HttpMethod.POST,
            new HttpEntity<>(headers),
            Void.class);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(
        testContext.getCategoriesCount() + testContext.getDefaultCategories().size(),
        categoryRepository.findAll().size());
  }

  @Test
  void testGetCategory() {
    final UserModel user = testContext.getRandomAdminOrUser();
    final Category category = testContext.getRandomCategoryByUserId(user.getUserId());
    headers.setBearerAuth(testContext.getAuthenticationToken(user.getUserId()));

    ResponseEntity<CategoryDto> response =
        restTemplate.exchange(
            basePath + "/categories/" + category.getCategoryId(),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            CategoryDto.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(category.getCategoryId(), response.getBody().getCategoryId());
    assertEquals(category.getUserId(), response.getBody().getUserId());
    assertEquals(category.getCategoryName(), response.getBody().getCategoryName());
    assertEquals(category.getDescription(), response.getBody().getDescription());
    if (category.getParentCategory() != null) {
      assertEquals(
          category.getParentCategory().getCategoryId(), response.getBody().getParentCategory());
    }
  }

  @Test
  void testUpdateCategory() {
    final UserModel user = testContext.getRandomAdminOrUser();
    final Category category = testContext.getRandomCategoryByUserId(user.getUserId());
    final Category parentCategory = testContext.getRandomCategoryByUserId(user.getUserId());
    CategoryUpdateRequestDto request =
        CategoryTestFactory.CategoryUpdateRequestBuilder.builder()
            .withParentCategory(
                category.getCategoryId().equals(parentCategory.getCategoryId())
                    ? null
                    : parentCategory.getCategoryId())
            .build();
    headers.setBearerAuth(testContext.getAuthenticationToken(user.getUserId()));

    ResponseEntity<CategoryDto> response =
        restTemplate.exchange(
            basePath + "/categories/" + category.getCategoryId(),
            HttpMethod.PUT,
            new HttpEntity<>(request, headers),
            CategoryDto.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(category.getCategoryId(), response.getBody().getCategoryId());
    assertEquals(
        request.description() != null ? request.description() : category.getDescription(),
        response.getBody().getDescription());
    assertEquals(
        request.parentId() != null
            ? request.parentId()
            : category.getParentCategory().getCategoryId(),
        response.getBody().getParentCategory());
    assertEquals(
        request.categoryName() != null ? request.categoryName() : category.getCategoryName(),
        response.getBody().getCategoryName());
    assertEquals(category.getUserId(), response.getBody().getUserId());
    assertEquals(
        request.description() != null ? request.description() : category.getDescription(),
        response.getBody().getDescription());
  }

  @Test
  void testDeleteCategory() {
    final UserModel user = testContext.getRandomUser();
    final Category category = testContext.getRandomCategoryByUserId(user.getUserId());
    headers.setBearerAuth(testContext.getAuthenticationToken(user.getUserId()));

    ResponseEntity<Void> response =
        restTemplate.exchange(
            basePath + "/categories/" + category.getCategoryId(),
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            Void.class);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }

  @Test
  void testCreateBudget() {
    final UUID userId = testContext.getRandomUser().getUserId();
    final Category category = testContext.getRandomCategoryByUserId(userId);
    final BudgetRequestDto request = testContext.createBudgetRequest(category);
    headers.setBearerAuth(testContext.getAuthenticationToken(userId));
    mockServer.mockExistentCurrency(request.currency());

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
    final BudgetCriteria criteria = testContext.createBudgetCriteria();
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
    final BudgetUpdateRequestDto request =
        BudgetTestFactory.BudgetUpdateRequestBuilder.defaultInstance();
    headers.setBearerAuth(testContext.getAuthenticationToken(userId));
    mockServer.mockExistentCurrency(request.currency());

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

  @Test
  void testGetCategoryTree() {
    final UUID userId = testContext.getRandomUser().getUserId();
    headers.setBearerAuth(testContext.getAuthenticationToken(userId));

    ResponseEntity<List<CategoryDto>> response =
        restTemplate.exchange(
            basePath + "/categories/users/%s/tree".formatted(userId),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<>() {});

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }
}
