package it.moneyverse.core.boot;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import it.moneyverse.core.services.CurrencyGrpcService;
import it.moneyverse.core.services.CurrencyServiceClient;
import it.moneyverse.core.services.CurrencyServiceGrpcClient;
import it.moneyverse.core.utils.properties.CurrencyServiceGrpcCircuitBreakerProperties;
import it.moneyverse.core.utils.properties.CurrencyServiceGrpcClientProperties;
import it.moneyverse.grpc.lib.CurrencyServiceGrpc;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(
    value = {
      CurrencyServiceGrpcClientProperties.class,
      CurrencyServiceGrpcCircuitBreakerProperties.class
    })
public class CurrencyServiceGrpcClientAutoConfiguration {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(CurrencyServiceGrpcClientAutoConfiguration.class);

  private final CurrencyServiceGrpcClientProperties clientProperties;
  private final CurrencyServiceGrpcCircuitBreakerProperties circuitBreakerProperties;

  public CurrencyServiceGrpcClientAutoConfiguration(
      CurrencyServiceGrpcClientProperties clientProperties,
      CurrencyServiceGrpcCircuitBreakerProperties circuitBreakerProperties) {
    this.clientProperties = clientProperties;
    this.circuitBreakerProperties = circuitBreakerProperties;
    LOGGER.info("Starting to load beans from {}", CurrencyServiceGrpcClientAutoConfiguration.class);
  }

  @Bean
  public ManagedChannel managedChannelCurrencyService() {
    return ManagedChannelBuilder.forAddress(clientProperties.getHost(), clientProperties.getPort())
        .usePlaintext()
        .build();
  }

  @Bean
  public CurrencyServiceGrpc.CurrencyServiceBlockingStub currencyServiceBlockingStub(
      ManagedChannel managedChannelCurrencyService) {
    return CurrencyServiceGrpc.newBlockingStub(managedChannelCurrencyService);
  }

  @Bean
  public CircuitBreaker currencyServiceCircuitBreaker(CircuitBreakerRegistry registry) {
    CircuitBreakerConfig currencyServiceConfig =
        CircuitBreakerConfig.custom()
            .failureRateThreshold(circuitBreakerProperties.getFailureRateThreshold())
            .waitDurationInOpenState(
                Duration.ofSeconds(circuitBreakerProperties.getWaitDurationInOpenState()))
            .slidingWindowSize(circuitBreakerProperties.getSlidingWindowSize())
            .build();
    return registry.circuitBreaker(
        CurrencyServiceGrpcCircuitBreakerProperties.CURRENCY_SERVICE_GRPC, currencyServiceConfig);
  }

  @Bean
  public CurrencyGrpcService currencyGrpcService(
      CurrencyServiceGrpc.CurrencyServiceBlockingStub stub) {
    return new CurrencyGrpcService(stub);
  }

  @Bean
  public CurrencyServiceClient currencyServiceClient(CurrencyGrpcService currencyGrpcService) {
    return new CurrencyServiceGrpcClient(currencyGrpcService);
  }
}
