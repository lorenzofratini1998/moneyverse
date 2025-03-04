package it.moneyverse.currency.runtime.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import it.moneyverse.core.model.dto.CurrencyDto;
import it.moneyverse.currency.model.CurrencyTestContext;
import it.moneyverse.test.annotations.MoneyverseTest;
import it.moneyverse.test.extensions.testcontainers.KeycloakContainer;
import it.moneyverse.test.extensions.testcontainers.PostgresContainer;
import it.moneyverse.test.utils.AbstractIntegrationTest;
import it.moneyverse.test.utils.properties.TestPropertyRegistry;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Container;

@MoneyverseTest
@TestPropertySource(properties = {"spring.runner.initializer.enabled=false"})
class CurrencyManagementControllerIT extends AbstractIntegrationTest {

  protected static CurrencyTestContext testContext;
  @Container static PostgresContainer postgresContainer = new PostgresContainer();
  @Container static KeycloakContainer keycloakContainer = new KeycloakContainer();

  private HttpHeaders headers;

  @DynamicPropertySource
  static void mappingProperties(DynamicPropertyRegistry registry) {
    new TestPropertyRegistry(registry)
        .withPostgres(postgresContainer)
        .withKeycloak(keycloakContainer)
        .withFlywayTestDirectory(tempDir);
  }

  @BeforeAll
  static void beforeAll() {
    testContext = new CurrencyTestContext(keycloakContainer).generateScript(tempDir);
  }

  @BeforeEach
  public void setup() {
    headers = new HttpHeaders();
  }

  @Test
  void testGetCurrencies() {
    final UUID userId = testContext.getRandomUser().getUserId();

    headers.setBearerAuth(testContext.getAuthenticationToken(userId));
    ResponseEntity<List<CurrencyDto>> response =
        restTemplate.exchange(
            basePath + "/currencies",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<>() {});

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(testContext.getCurrencies().size(), response.getBody().size());
  }
}
