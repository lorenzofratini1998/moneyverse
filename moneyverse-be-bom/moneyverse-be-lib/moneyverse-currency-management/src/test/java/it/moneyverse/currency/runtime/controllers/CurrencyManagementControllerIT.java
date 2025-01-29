package it.moneyverse.currency.runtime.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import it.moneyverse.currency.model.dto.CurrencyDto;
import it.moneyverse.currency.utils.CurrencyTestContext;
import it.moneyverse.test.annotations.IntegrationTest;
import it.moneyverse.test.extensions.testcontainers.KeycloakContainer;
import it.moneyverse.test.extensions.testcontainers.PostgresContainer;
import it.moneyverse.test.utils.AbstractIntegrationTest;
import it.moneyverse.test.utils.properties.TestPropertyRegistry;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Container;

@IntegrationTest
@TestPropertySource(
    properties = {
            "spring.runner.initializer.enabled=false"
    })
class CurrencyManagementControllerIT extends AbstractIntegrationTest {

  protected static CurrencyTestContext testContext;
  @Container static PostgresContainer postgresContainer = new PostgresContainer();
  @Container static KeycloakContainer keycloakContainer = new KeycloakContainer();

  private HttpHeaders headers;

  @DynamicPropertySource
  static void mappingProperties(DynamicPropertyRegistry registry) {
    new TestPropertyRegistry(registry)
        .withPostgres(postgresContainer)
        .withKeycloak(keycloakContainer);
  }

  @BeforeAll
  static void beforeAll() {
    testContext =
        new CurrencyTestContext()
            .generateScript(tempDir)
            .insertUsersIntoKeycloak(keycloakContainer);
  }

  @BeforeEach
  public void setup() {
    headers = new HttpHeaders();
  }

  @Test
  void testGetCurrencies() {
    final String username = testContext.getRandomUser().getUsername();

    headers.setBearerAuth(testContext.getAuthenticationToken(username));
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
