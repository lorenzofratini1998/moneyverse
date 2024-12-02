package it.moneyverse.account.services;

import it.moneyverse.core.services.UserServiceClient;
import it.moneyverse.grpc.lib.UserRequest;
import it.moneyverse.grpc.lib.UserResponse;
import it.moneyverse.grpc.lib.UserServiceGrpc;
import org.springframework.stereotype.Service;

@Service
public class UserServiceGrpcClient implements UserServiceClient {

  private final UserServiceGrpc.UserServiceBlockingStub stub;

  public UserServiceGrpcClient(UserServiceGrpc.UserServiceBlockingStub stub) {
    this.stub = stub;
  }

  @Override
  public Boolean checkIfUserExists(String username) {
    final UserResponse response =
        stub.checkIfUserExists(UserRequest.newBuilder().setUsername(username).build());
    return response.getExists();
  }
}
