package it.moneyverse.account.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import it.moneyverse.account.runtime.messages.AccountConsumer;
import it.moneyverse.core.utils.properties.UserServiceGrpcCircuitBreakerProperties;
import it.moneyverse.grpc.lib.UserResponse;
import it.moneyverse.test.extensions.grpc.GrpcMockUserService;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.test.utils.properties.TestPropertyRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/** Unit tests for {@link UserServiceGrpcClient} */
@SpringBootTest(
    properties = {
      "spring.autoconfigure.exclude=it.moneyverse.core.boot.SecurityAutoConfiguration, it.moneyverse.core.boot.DatasourceAutoConfiguration, it.moneyverse.core.boot.KafkaAutoConfiguration",
      "logging.level.org.grpcmock.GrpcMock=WARN"
    })
@EmbeddedKafka
@ExtendWith(MockitoExtension.class)
class UserServiceGrpcClientTest {

  @RegisterExtension static GrpcMockUserService mockUserService = new GrpcMockUserService();

  @Autowired private UserServiceGrpcClient userServiceClient;
  @Autowired private CircuitBreakerRegistry registry;

  private CircuitBreaker circuitBreaker;

  @MockitoBean private AccountConsumer accountConsumer;
  @MockitoBean private AccountProducer accountProducer;

  @DynamicPropertySource
  static void mappingProperties(DynamicPropertyRegistry registry) {
    new TestPropertyRegistry(registry)
        .withGrpcUserService(mockUserService.getHost(), mockUserService.getPort());
  }

  @BeforeEach
  void setUp() {
    circuitBreaker =
        registry.circuitBreaker(UserServiceGrpcCircuitBreakerProperties.USER_SERVICE_GRPC);
    circuitBreaker.transitionToClosedState();
  }

  @AfterEach
  void tearDown() {
    circuitBreaker.reset();
  }

  @Test
  void givenUsername_WhenCheckIfUserExists_ThenReturnTrue() {
    String username = RandomUtils.randomUUID().toString();
    mockUserService.mockExistentUser();

    Boolean exists = userServiceClient.checkIfUserExists(username);

    assertTrue(exists);
  }

  @Test
  void givenCircuitBreakerOpen_WhenCheckIfUserExists_ThenFallbackMethodIsTriggered() {
    String username = RandomUtils.randomUUID().toString();
    circuitBreaker.transitionToOpenState();

    Boolean exists = userServiceClient.checkIfUserExists(username);
    assertFalse(exists);
  }
}
