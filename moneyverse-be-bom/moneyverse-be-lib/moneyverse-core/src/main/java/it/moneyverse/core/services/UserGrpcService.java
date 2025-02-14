package it.moneyverse.core.services;

import static it.moneyverse.core.utils.constants.CacheConstants.USERS_CACHE;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import it.moneyverse.core.model.dto.UserDto;
import it.moneyverse.core.utils.properties.UserServiceGrpcCircuitBreakerProperties;
import it.moneyverse.grpc.lib.UserRequest;
import it.moneyverse.grpc.lib.UserResponse;
import it.moneyverse.grpc.lib.UserServiceGrpc;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class UserGrpcService {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserGrpcService.class);
  private final UserServiceGrpc.UserServiceBlockingStub stub;

  public UserGrpcService(UserServiceGrpc.UserServiceBlockingStub stub) {
    this.stub = stub;
  }

  @Cacheable(value = USERS_CACHE, key = "#userId", unless = "#result == null")
  @CircuitBreaker(
      name = UserServiceGrpcCircuitBreakerProperties.USER_SERVICE_GRPC,
      fallbackMethod = "fallbackGetUserById")
  public Optional<UserDto> getUserById(UUID userId) {
    final UserResponse response =
        stub.getUserById(UserRequest.newBuilder().setUserId(userId.toString()).build());
    if (response == null || isEmptyResponse(response)) {
      return Optional.empty();
    }
    return Optional.of(
        UserDto.builder()
            .withUserId(UUID.fromString(response.getUserId()))
            .withEmail(response.getEmail())
            .withFirstName(response.getFirstName())
            .withLastName(response.getLastName())
            .build());
  }

  private boolean isEmptyResponse(UserResponse response) {
    return response.getUserId().isEmpty()
        && response.getEmail().isEmpty()
        && response.getFirstName().isEmpty()
        && response.getLastName().isEmpty();
  }

  protected Optional<UserDto> fallbackGetUserById(UUID userId, Throwable throwable) {
    LOGGER.error(
        "Impossible to contact the UserService to retrieve the user {}. Returning FALSE as fallback: {}",
        userId,
        throwable.getMessage());
    return Optional.empty();
  }
}
