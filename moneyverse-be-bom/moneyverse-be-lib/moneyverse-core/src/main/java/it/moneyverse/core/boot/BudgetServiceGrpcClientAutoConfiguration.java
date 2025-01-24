package it.moneyverse.core.boot;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import it.moneyverse.core.utils.properties.BudgetServiceGrpcCircuitBreakerProperties;
import it.moneyverse.core.utils.properties.BudgetServiceGrpcClientProperties;
import it.moneyverse.grpc.lib.BudgetServiceGrpc;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(
    value = {
      BudgetServiceGrpcClientProperties.class,
      BudgetServiceGrpcCircuitBreakerProperties.class
    })
public class BudgetServiceGrpcClientAutoConfiguration {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(BudgetServiceGrpcClientAutoConfiguration.class);

  private final BudgetServiceGrpcClientProperties clientProperties;
  private final BudgetServiceGrpcCircuitBreakerProperties circuitBreakerProperties;

  public BudgetServiceGrpcClientAutoConfiguration(
      BudgetServiceGrpcClientProperties clientProperties,
      BudgetServiceGrpcCircuitBreakerProperties circuitBreakerProperties) {
    this.clientProperties = clientProperties;
    this.circuitBreakerProperties = circuitBreakerProperties;
    LOGGER.info("Starting to load beans from {}", BudgetServiceGrpcClientAutoConfiguration.class);
  }

  @Bean
  public ManagedChannel managedChannelBudgetService() {
    return ManagedChannelBuilder.forAddress(clientProperties.getHost(), clientProperties.getPort())
        .usePlaintext()
        .build();
  }

  @Bean
  public BudgetServiceGrpc.BudgetServiceBlockingStub budgetServiceBlockingStub(
      ManagedChannel managedChannelBudgetService) {
    return BudgetServiceGrpc.newBlockingStub(managedChannelBudgetService);
  }

  @Bean
  public CircuitBreaker budgetServiceCircuitBreaker(CircuitBreakerRegistry registry) {
    CircuitBreakerConfig budgetServiceConfig =
        CircuitBreakerConfig.custom()
            .failureRateThreshold(circuitBreakerProperties.getFailureRateThreshold())
            .waitDurationInOpenState(
                Duration.ofSeconds(circuitBreakerProperties.getWaitDurationInOpenState()))
            .slidingWindowSize(circuitBreakerProperties.getSlidingWindowSize())
            .build();
    return registry.circuitBreaker(
        BudgetServiceGrpcCircuitBreakerProperties.BUDGET_SERVICE_GRPC, budgetServiceConfig);
  }
}
