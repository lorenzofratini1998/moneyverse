package it.moneyverse.analytics.runtime.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import it.moneyverse.analytics.model.AnalyticsTestContext;
import it.moneyverse.analytics.model.AnalyticsTestFactory;
import it.moneyverse.analytics.model.dto.AccountAnalyticsDistributionDto;
import it.moneyverse.analytics.model.dto.AccountAnalyticsKpiDto;
import it.moneyverse.analytics.model.dto.AccountAnalyticsTrendDto;
import it.moneyverse.analytics.model.dto.FilterDto;
import it.moneyverse.analytics.runtime.batch.TransactionEventScheduler;
import it.moneyverse.test.annotations.MoneyverseTest;
import it.moneyverse.test.extensions.testcontainers.ClickhouseContainer;
import it.moneyverse.test.extensions.testcontainers.KafkaContainer;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Container;

@MoneyverseTest
public class AccountAnalyticsControllerIT extends AbstractIntegrationTest {
  protected static AnalyticsTestContext testContext;
  @Container static PostgresContainer postgresContainer = new PostgresContainer();
  @Container static KeycloakContainer keycloakContainer = new KeycloakContainer();
  @Container static KafkaContainer kafkaContainer = new KafkaContainer();
  @Container static ClickhouseContainer clickhouseContainer = new ClickhouseContainer();

  @MockitoBean private TransactionEventScheduler transactionEventScheduler;
  private HttpHeaders headers;

  @DynamicPropertySource
  static void mappingProperties(DynamicPropertyRegistry registry) {
    new TestPropertyRegistry(registry)
        .withPostgres(postgresContainer)
        .withKeycloak(keycloakContainer)
        .withKafkaContainer(kafkaContainer)
        .withClickhouse(clickhouseContainer);
    registry.add(
        "spring.flyway.clickhouse.locations",
        () ->
            "classpath:db/migration/clickhouse,filesystem:%s"
                .formatted(tempDir.toAbsolutePath().toString()));
  }

  @BeforeAll
  static void beforeAll() {
    testContext = new AnalyticsTestContext(keycloakContainer).generateScript(tempDir);
  }

  @BeforeEach
  void beforeEach() {
    headers = new HttpHeaders();
  }

  @Test
  void testCalculateAccountKpi() {
    final UUID userId = testContext.getRandomUser().getUserId();
    headers.setBearerAuth(testContext.getAuthenticationToken(userId));
    final FilterDto parameters =
        AnalyticsTestFactory.createFilter(userId, testContext.getTransactionEvents());

    ResponseEntity<AccountAnalyticsKpiDto> response =
        restTemplate.exchange(
            basePath + "/accounts/kpi",
            HttpMethod.POST,
            new HttpEntity<>(parameters, headers),
            AccountAnalyticsKpiDto.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  void testCalculateAccountDistribution() {
    final UUID userId = testContext.getRandomUser().getUserId();
    headers.setBearerAuth(testContext.getAuthenticationToken(userId));
    final FilterDto parameters =
        AnalyticsTestFactory.createFilter(userId, testContext.getTransactionEvents());

    ResponseEntity<List<AccountAnalyticsDistributionDto>> response =
        restTemplate.exchange(
            basePath + "/accounts/distribution",
            HttpMethod.POST,
            new HttpEntity<>(parameters, headers),
            new ParameterizedTypeReference<>() {});
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  void testCalculateAccountTrend() {
    final UUID userId = testContext.getRandomUser().getUserId();
    headers.setBearerAuth(testContext.getAuthenticationToken(userId));
    final FilterDto parameters =
        AnalyticsTestFactory.createFilter(userId, testContext.getTransactionEvents());

    ResponseEntity<List<AccountAnalyticsTrendDto>> response =
        restTemplate.exchange(
            basePath + "/accounts/trend",
            HttpMethod.POST,
            new HttpEntity<>(parameters, headers),
            new ParameterizedTypeReference<>() {});
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
  }
}
