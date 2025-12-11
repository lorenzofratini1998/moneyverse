package it.moneyverse.core.boot;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.internal.InMemoryCircuitBreakerRegistry;
import io.grpc.ManagedChannel;
import it.moneyverse.core.utils.properties.UserServiceGrpcClientProperties;
import it.moneyverse.grpc.lib.UserServiceGrpc.UserServiceBlockingStub;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

public class UserServiceGrpcClientAutoConfigurationTest {

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withUserConfiguration(UserServiceGrpcClientAutoConfiguration.class)
          .withPropertyValues(
              "%s=%s"
                  .formatted(UserServiceGrpcClientProperties.USER_SERVICE_CLIENT_HOST, "localhost"),
              "%s=%s".formatted(UserServiceGrpcClientProperties.USER_SERVICE_CLIENT_PORT, 0))
          .withBean(InMemoryCircuitBreakerRegistry.class);

  @Test
  void testUserServiceGrpcClientBeanCreation() {
    contextRunner.run(
        applicationContext -> {
          assertThat(applicationContext).hasSingleBean(ManagedChannel.class);
          assertThat(applicationContext).hasSingleBean(UserServiceBlockingStub.class);
          assertThat(applicationContext).hasSingleBean(CircuitBreaker.class);
        });
  }
}
