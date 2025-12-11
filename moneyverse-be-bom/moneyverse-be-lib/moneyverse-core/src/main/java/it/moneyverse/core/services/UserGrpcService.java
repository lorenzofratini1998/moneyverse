package it.moneyverse.core.services;

import static it.moneyverse.core.utils.constants.CacheConstants.USERS_CACHE;
import static it.moneyverse.core.utils.constants.CacheConstants.USER_PREFERENCES_CACHE;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import it.moneyverse.core.model.dto.PreferenceDto;
import it.moneyverse.core.model.dto.UserDto;
import it.moneyverse.core.model.dto.UserPreferenceDto;
import it.moneyverse.core.utils.properties.UserServiceGrpcCircuitBreakerProperties;
import it.moneyverse.grpc.lib.*;
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
        "Impossible to contact the UserService to retrieve the user {}. Returning EMPTY as fallback: {}",
        userId,
        throwable.getMessage());
    return Optional.empty();
  }

  @Cacheable(
      value = USER_PREFERENCES_CACHE,
      key = "#userId + '_' + #preferenceName",
      unless = "#result == null")
  @CircuitBreaker(
      name = UserServiceGrpcCircuitBreakerProperties.USER_SERVICE_GRPC,
      fallbackMethod = "fallbackGetUserPreference")
  public Optional<UserPreferenceDto> getUserPreference(UUID userId, String preferenceName) {
    final UserPreferenceResponse response =
        stub.getUserPreference(
            UserPreferenceRequest.newBuilder()
                .setUserId(userId.toString())
                .setPreferenceKey(preferenceName)
                .build());
    if (response == null || response.getPreferenceValue().isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(
        UserPreferenceDto.builder()
            .withUserId(userId)
            .withPreference(PreferenceDto.builder().withName(preferenceName).build())
            .withValue(response.getPreferenceValue())
            .build());
  }

  protected Optional<UserPreferenceDto> fallbackGetUserPreference(
      UUID userId, String preferenceName, Throwable throwable) {
    LOGGER.error(
        "Impossible to contact the UserService to retrieve the preference {} for user {}. Returning EMPTY as fallback: {}",
        preferenceName,
        userId,
        throwable.getMessage());
    return Optional.empty();
  }
}
