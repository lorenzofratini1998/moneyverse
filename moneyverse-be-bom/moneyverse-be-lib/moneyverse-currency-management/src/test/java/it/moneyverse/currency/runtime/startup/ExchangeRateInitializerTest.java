package it.moneyverse.currency.runtime.startup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import it.moneyverse.core.boot.DatasourceAutoConfiguration;
import it.moneyverse.core.boot.KafkaAutoConfiguration;
import it.moneyverse.core.boot.SecurityAutoConfiguration;
import it.moneyverse.currency.model.repositories.CurrencyRepository;
import it.moneyverse.currency.model.repositories.ExchangeRateRepository;
import it.moneyverse.currency.services.ExchangeRateManagementService;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;

@DataJpaTest(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:testdb",
      "spring.datasource.driverClassName=org.h2.Driver",
      "spring.datasource.username=sa",
      "spring.datasource.password=password",
      "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
      "spring.jpa.hibernate.ddl-auto=create",
      "spring.jpa.properties.hibernate.show_sql=false",
      "flyway.enabled=false"
    },
    excludeAutoConfiguration = {
      FlywayAutoConfiguration.class,
      SecurityAutoConfiguration.class,
      DatasourceAutoConfiguration.class,
      KafkaAutoConfiguration.class
    })
@ExtendWith(MockitoExtension.class)
class ExchangeRateInitializerTest {

  @Autowired private ExchangeRateRepository exchangeRateRepository;
  @Autowired private CurrencyRepository currencyRepository;
  @MockitoBean private RestTemplate restTemplate;
  private ExchangeRateInitializer exchangeRateInitializer;
  private final Resource resource =
      new ClassPathResource("files/ExchangeRateInitializer_Response.xml");

  @BeforeEach
  void setup() {
    exchangeRateInitializer =
        new ExchangeRateInitializer(
            new ExchangeRateManagementService(
                restTemplate, currencyRepository, exchangeRateRepository));
  }

  @Test
  void testRun() throws IOException {
    String xmlContent = new String(Files.readAllBytes(resource.getFile().toPath()));
    when(restTemplate.getForEntity(anyString(), eq(String.class)))
        .thenReturn(new ResponseEntity<>(xmlContent, HttpStatus.OK));

    exchangeRateInitializer.run(new DefaultApplicationArguments());

    assertThat(exchangeRateRepository.findAll()).isNotEmpty();
  }
}
