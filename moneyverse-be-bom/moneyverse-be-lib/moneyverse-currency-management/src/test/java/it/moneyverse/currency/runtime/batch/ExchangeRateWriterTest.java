package it.moneyverse.currency.runtime.batch;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.moneyverse.core.boot.DatasourceAutoConfiguration;
import it.moneyverse.core.boot.SecurityAutoConfiguration;
import it.moneyverse.currency.model.CurrencyTestFactory;
import it.moneyverse.currency.model.entities.ExchangeRate;
import it.moneyverse.currency.model.repositories.ExchangeRateRepository;
import it.moneyverse.test.utils.RandomUtils;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.Chunk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:testdb",
      "spring.datasource.driverClassName=org.h2.Driver",
      "spring.datasource.username=sa",
      "spring.datasource.password=password",
      "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
      "spring.jpa.hibernate.ddl-auto=create",
      "spring.jpa.properties.hibernate.show_sql=false",
      "flyway.enabled=false",
      "spring.runner.initializer.enabled=false"
    },
    excludeAutoConfiguration = {
      FlywayAutoConfiguration.class,
      SecurityAutoConfiguration.class,
      DatasourceAutoConfiguration.class
    })
class ExchangeRateWriterTest {

  @Autowired private ExchangeRateRepository exchangeRateRepository;

  private ExchangeRateWriter exchangeRateWriter;

  @BeforeEach
  void setUp() {
    exchangeRateWriter = new ExchangeRateWriter(exchangeRateRepository);
  }

  @Test
  void testWrite() {
    List<ExchangeRate> exchangeRates = new ArrayList<>();
    for (int i = 0; i < RandomUtils.randomInteger(1, 10); i++) {
      exchangeRates.add(CurrencyTestFactory.fakeExchangeRate());
    }
    Chunk<List<ExchangeRate>> chunk = new Chunk<>(List.of(exchangeRates));
    exchangeRateWriter.write(chunk);

    assertEquals(exchangeRates.size(), exchangeRateRepository.findAll().size());
  }
}
