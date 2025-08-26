package it.moneyverse.currency.runtime.server;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import it.moneyverse.core.boot.*;
import it.moneyverse.currency.model.CurrencyTestContext;
import it.moneyverse.currency.model.entities.Currency;
import it.moneyverse.currency.model.entities.ExchangeRate;
import it.moneyverse.currency.model.repositories.CurrencyRepository;
import it.moneyverse.currency.model.repositories.ExchangeRateRepository;
import it.moneyverse.grpc.lib.*;
import it.moneyverse.test.utils.RandomUtils;
import jakarta.persistence.EntityManager;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
      "flyway.enabled=false"
    },
    excludeAutoConfiguration = {
      FlywayAutoConfiguration.class,
      SecurityAutoConfiguration.class,
      DatasourceAutoConfiguration.class,
    })
@ExtendWith(MockitoExtension.class)
class CurrencyServerTest {

  private CurrencyServiceGrpc.CurrencyServiceBlockingStub stub;
  private ManagedChannel channel;
  private CurrencyServer currencyServer;
  @Autowired private CurrencyRepository currencyRepository;
  @Mock private ExchangeRateRepository exchangeRateRepository;
  @Autowired EntityManager entityManager;

  static CurrencyTestContext testContext;

  @BeforeAll
  public static void beforeAll() {
    testContext = new CurrencyTestContext();
  }

  @BeforeEach
  void setup() throws IOException {
    String serverName = InProcessServerBuilder.generateName();
    channel = InProcessChannelBuilder.forName(serverName).directExecutor().build();
    Server server =
        InProcessServerBuilder.forName(serverName)
            .addService(
                new CurrencyManagementGrpcService(currencyRepository, exchangeRateRepository))
            .directExecutor()
            .build()
            .start();
    stub = CurrencyServiceGrpc.newBlockingStub(channel);
    currencyServer =
        new CurrencyServer(
            RandomUtils.randomBigDecimal().intValue(), currencyRepository, exchangeRateRepository);
    currencyServer.start();
    testContext.getCurrencies().forEach(currency -> currency.setCurrencyId(null));
    for (Currency currency : testContext.getCurrencies()) {
      entityManager.persist(currency);
    }
    entityManager.flush();
  }

  @AfterEach
  void teardown() throws InterruptedException {
    if (channel != null) {
      channel.shutdown();
    }
    currencyServer.stop();
  }

  @Test
  void givenCurrencyCode_WhenCurrencyExists_ThenReturnCurrency() {
    String code = testContext.getRandomCurrency().getCode();
    CurrencyRequest request = CurrencyRequest.newBuilder().setIsoCode(code).build();

    CurrencyResponse response = stub.getCurrencyByCode(request);

    assertEquals(code, response.getIsoCode());
  }

  @Test
  void givenCurrencyCode_WhenCurrencyDisabled_ThenReturnEmptyResponse() {
    String code = testContext.getRandomDisabledCurrency().getCode();
    CurrencyRequest request = CurrencyRequest.newBuilder().setIsoCode(code).build();

    CurrencyResponse response = stub.getCurrencyByCode(request);

    assertEquals("", response.getIsoCode());
  }

  @Test
  void givenCurrencyCode_WhenCurrencyNotExists_ThenReturnEmptyResponse() {
    String code = RandomUtils.randomString(3).toUpperCase();
    CurrencyRequest request = CurrencyRequest.newBuilder().setIsoCode(code).build();

    CurrencyResponse response = stub.getCurrencyByCode(request);

    assertEquals("", response.getIsoCode());
  }

  @Test
  void givenSameCurrencies_WhenGetExchangeRate_ThenBuildExchangeResponse() {
    String currency = RandomUtils.randomCurrency();
    ExchangeRateRequest request =
        ExchangeRateRequest.newBuilder()
            .setFromCurrency(currency)
            .setToCurrency(currency)
            .setDate(RandomUtils.randomDate().toString())
            .build();
    ExchangeRateResponse response = stub.getExchangeRate(request);

    assertEquals(1.0, response.getRate());
  }

  @Test
  void givenFromEurCurrency_WhenGetExchangeRate_ThenBuildExchangeResponse() {
    String toCurrency = RandomUtils.randomCurrency();
    LocalDate date = RandomUtils.randomDate();
    ExchangeRateRequest request =
        ExchangeRateRequest.newBuilder()
            .setFromCurrency("EUR")
            .setToCurrency(toCurrency)
            .setDate(date.toString())
            .build();
    ExchangeRate exchangeRate = new ExchangeRate();
    exchangeRate.setRate(RandomUtils.randomBigDecimal());
    when(exchangeRateRepository.findExchangeRateByCurrencyFromAndCurrencyToAndDate(
            request.getFromCurrency(), request.getToCurrency(), date))
        .thenReturn(Optional.of(exchangeRate));
    ExchangeRateResponse response = stub.getExchangeRate(request);

    assertEquals(exchangeRate.getRate().doubleValue(), response.getRate());
  }

  @Test
  void givenToEurCurrency_WhenGetExchangeRate_ThenBuildExchangeResponse() {
    String fromCurrency = RandomUtils.randomCurrency();
    LocalDate date = RandomUtils.randomDate();
    ExchangeRateRequest request =
        ExchangeRateRequest.newBuilder()
            .setFromCurrency(fromCurrency)
            .setToCurrency("EUR")
            .setDate(date.toString())
            .build();
    ExchangeRate exchangeRate = new ExchangeRate();
    exchangeRate.setRate(RandomUtils.randomBigDecimal());
    when(exchangeRateRepository.findExchangeRateByCurrencyFromAndCurrencyToAndDate(
            request.getFromCurrency(), request.getToCurrency(), date))
        .thenReturn(Optional.of(exchangeRate));
    ExchangeRateResponse response = stub.getExchangeRate(request);

    assertEquals(
        BigDecimal.ONE.divide(exchangeRate.getRate(), 4, RoundingMode.HALF_UP).doubleValue(),
        response.getRate());
  }

  @Test
  void givenExchangeRate_WhenGetExchangeRate_ThenBuildExchangeResponse() {
    String fromCurrency = RandomUtils.randomCurrency();
    String toCurrency = RandomUtils.randomCurrency();
    LocalDate date = RandomUtils.randomDate();
    ExchangeRateRequest request =
        ExchangeRateRequest.newBuilder()
            .setFromCurrency(fromCurrency)
            .setToCurrency(toCurrency)
            .setDate(date.toString())
            .build();

    ExchangeRate exchangeRateFrom = new ExchangeRate();
    exchangeRateFrom.setRate(RandomUtils.randomBigDecimal());
    when(exchangeRateRepository.findExchangeRateByCurrencyFromAndCurrencyToAndDate(
            "EUR", request.getFromCurrency(), date))
        .thenReturn(Optional.of(exchangeRateFrom));
    ExchangeRate exchangeRateTo = new ExchangeRate();
    exchangeRateTo.setRate(RandomUtils.randomBigDecimal());
    when(exchangeRateRepository.findExchangeRateByCurrencyFromAndCurrencyToAndDate(
            "EUR", request.getToCurrency(), date))
        .thenReturn(Optional.of(exchangeRateTo));
    ExchangeRateResponse response = stub.getExchangeRate(request);

    assertEquals(
        exchangeRateTo
            .getRate()
            .divide(exchangeRateFrom.getRate(), RoundingMode.HALF_UP)
            .doubleValue(),
        response.getRate());
  }

  @Test
  void givenExchangeRate_WhenGetExchangeRate_ThenBuildExchangeResponseWithZeroRate() {
    String fromCurrency = RandomUtils.randomCurrency();
    String toCurrency = RandomUtils.randomCurrency();
    LocalDate date = RandomUtils.randomDate();
    ExchangeRateRequest request =
        ExchangeRateRequest.newBuilder()
            .setFromCurrency(fromCurrency)
            .setToCurrency(toCurrency)
            .setDate(date.toString())
            .build();
    when(exchangeRateRepository.findExchangeRateByCurrencyFromAndCurrencyToAndDate(
            "EUR", request.getFromCurrency(), date))
        .thenReturn(Optional.empty());
    ExchangeRateResponse response = stub.getExchangeRate(request);

    assertEquals(0.0, response.getRate());
  }
}
