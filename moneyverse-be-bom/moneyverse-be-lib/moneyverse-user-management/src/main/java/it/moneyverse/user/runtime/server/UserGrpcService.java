package it.moneyverse.user.runtime.server;

import io.grpc.stub.StreamObserver;
import it.moneyverse.core.model.dto.UserDto;
import it.moneyverse.grpc.lib.*;
import it.moneyverse.user.model.entities.UserPreference;
import it.moneyverse.user.model.repositories.UserPreferenceRepository;
import it.moneyverse.user.services.KeycloakService;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserGrpcService.class);
  private final KeycloakService keycloakService;
  private final UserPreferenceRepository userPreferenceRepository;

  public UserGrpcService(
      KeycloakService keycloakService, UserPreferenceRepository userPreferenceRepository) {
    this.keycloakService = keycloakService;
    this.userPreferenceRepository = userPreferenceRepository;
  }

  @Override
  public void getUserById(UserRequest request, StreamObserver<UserResponse> responseObserver) {
    Optional<UserDto> user = keycloakService.getUserById(UUID.fromString(request.getUserId()));
    UserResponse response = getUserResponse(request.getUserId(), user);
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  private UserResponse getUserResponse(String userId, Optional<UserDto> user) {
    if (user.isEmpty()) {
      LOGGER.error("User with id {} does not exist", userId);
      return UserResponse.getDefaultInstance();
    }
    return UserResponse.newBuilder()
        .setUserId(user.get().getUserId().toString())
        .setFirstName(user.get().getFirstName())
        .setLastName(user.get().getLastName())
        .setEmail(user.get().getEmail())
        .build();
  }

  @Override
  public void getUserPreference(
      UserPreferenceRequest request, StreamObserver<UserPreferenceResponse> responseObserver) {
    Optional<UserPreference> userPreference =
        userPreferenceRepository.findUserPreferenceByUserIdAndPreference_Name(
            UUID.fromString(request.getUserId()), request.getPreferenceKey());
    UserPreferenceResponse response;
    if (userPreference.isEmpty()) {
      LOGGER.error(
          "Preference {} is not set for user {}", request.getPreferenceKey(), request.getUserId());
      response = UserPreferenceResponse.getDefaultInstance();
    } else {
      response =
          UserPreferenceResponse.newBuilder()
              .setPreferenceValue(userPreference.get().getValue())
              .build();
    }
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}
