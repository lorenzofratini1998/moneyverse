package it.moneyverse.core.boot;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import it.moneyverse.core.services.UserGrpcService;
import it.moneyverse.core.services.UserServiceGrpcClient;
import it.moneyverse.core.utils.properties.UserServiceGrpcCircuitBreakerProperties;
import it.moneyverse.core.utils.properties.UserServiceGrpcClientProperties;
import it.moneyverse.grpc.lib.UserServiceGrpc;
import it.moneyverse.grpc.lib.UserServiceGrpc.UserServiceBlockingStub;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(
    value = {UserServiceGrpcClientProperties.class, UserServiceGrpcCircuitBreakerProperties.class})
public class UserServiceGrpcClientAutoConfiguration {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(UserServiceGrpcClientAutoConfiguration.class);

  private final UserServiceGrpcClientProperties clientProperties;
  private final UserServiceGrpcCircuitBreakerProperties circuitBreakerProperties;

  public UserServiceGrpcClientAutoConfiguration(
      UserServiceGrpcClientProperties clientProperties,
      UserServiceGrpcCircuitBreakerProperties circuitBreakerProperties) {
    this.clientProperties = clientProperties;
    this.circuitBreakerProperties = circuitBreakerProperties;
    LOGGER.info("Starting to load beans from {}", UserServiceGrpcClientAutoConfiguration.class);
  }

  @Bean
  public ManagedChannel managedChannelUserService() {
    return ManagedChannelBuilder.forAddress(clientProperties.getHost(), clientProperties.getPort())
        .usePlaintext()
        .build();
  }

  @Bean
  public UserServiceBlockingStub userServiceBlockingStub(ManagedChannel managedChannelUserService) {
    return UserServiceGrpc.newBlockingStub(managedChannelUserService);
  }

  @Bean
  public CircuitBreaker userServiceGrpcCircuitBreaker(CircuitBreakerRegistry registry) {
    CircuitBreakerConfig userServiceConfig =
        CircuitBreakerConfig.custom()
            .failureRateThreshold(circuitBreakerProperties.getFailureRateThreshold())
            .waitDurationInOpenState(
                Duration.ofSeconds(circuitBreakerProperties.getWaitDurationInOpenState()))
            .slidingWindowSize(circuitBreakerProperties.getSlidingWindowSize())
            .build();
    return registry.circuitBreaker(
        UserServiceGrpcCircuitBreakerProperties.USER_SERVICE_GRPC, userServiceConfig);
  }

  @Bean
  public UserGrpcService userGrpcService(UserServiceBlockingStub userServiceBlockingStub) {
    return new UserGrpcService(userServiceBlockingStub);
  }

  @Bean
  public UserServiceGrpcClient userServiceGrpcClient(UserGrpcService userGrpcService) {
    return new UserServiceGrpcClient(userGrpcService);
  }
}
