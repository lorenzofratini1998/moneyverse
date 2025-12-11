package it.moneyverse.core.boot;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import it.moneyverse.core.utils.properties.AccountServiceGrpcCircuitBreakerProperties;
import it.moneyverse.core.utils.properties.AccountServiceGrpcClientProperties;
import it.moneyverse.grpc.lib.AccountServiceGrpc;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(
    value = {
      AccountServiceGrpcClientProperties.class,
      AccountServiceGrpcCircuitBreakerProperties.class
    })
public class AccountServiceGrpcClientAutoConfiguration {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(AccountServiceGrpcClientAutoConfiguration.class);

  private final AccountServiceGrpcClientProperties clientProperties;
  private final AccountServiceGrpcCircuitBreakerProperties circuitBreakerProperties;

  public AccountServiceGrpcClientAutoConfiguration(
      AccountServiceGrpcClientProperties clientProperties,
      AccountServiceGrpcCircuitBreakerProperties circuitBreakerProperties) {
    this.clientProperties = clientProperties;
    this.circuitBreakerProperties = circuitBreakerProperties;
    LOGGER.info("Starting to load beans from {}", AccountServiceGrpcClientAutoConfiguration.class);
  }

  @Bean
  public ManagedChannel managedChannelAccountService() {
    return ManagedChannelBuilder.forAddress(clientProperties.getHost(), clientProperties.getPort())
        .usePlaintext()
        .build();
  }

  @Bean
  public AccountServiceGrpc.AccountServiceBlockingStub accountServiceBlockingStub(
      ManagedChannel managedChannelAccountService) {
    return AccountServiceGrpc.newBlockingStub(managedChannelAccountService);
  }

  @Bean
  public CircuitBreaker accountServiceCircuitBreaker(CircuitBreakerRegistry registry) {
    CircuitBreakerConfig accountServiceConfig =
        CircuitBreakerConfig.custom()
            .failureRateThreshold(circuitBreakerProperties.getFailureRateThreshold())
            .waitDurationInOpenState(
                Duration.ofSeconds(circuitBreakerProperties.getWaitDurationInOpenState()))
            .slidingWindowSize(circuitBreakerProperties.getSlidingWindowSize())
            .build();
    return registry.circuitBreaker(
        AccountServiceGrpcCircuitBreakerProperties.ACCOUNT_SERVICE_GRPC, accountServiceConfig);
  }
}
