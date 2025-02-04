package it.moneyverse.user.runtime.server;

import io.grpc.stub.StreamObserver;
import it.moneyverse.grpc.lib.UserRequest;
import it.moneyverse.grpc.lib.UserResponse;
import it.moneyverse.grpc.lib.UserServiceGrpc;
import it.moneyverse.user.services.KeycloakService;
import java.util.UUID;

class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {

  private final KeycloakService keycloakService;

  public UserGrpcService(KeycloakService keycloakService) {
    this.keycloakService = keycloakService;
  }

  @Override
  public void checkIfUserExists(
      UserRequest request, StreamObserver<UserResponse> responseObserver) {
    boolean exists = keycloakService.getUserById(UUID.fromString(request.getUserId())).isPresent();
    UserResponse response = UserResponse.newBuilder().setExists(exists).build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}
