package it.moneyverse.core.services;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import it.moneyverse.core.utils.properties.UserServiceGrpcCircuitBreakerProperties;
import it.moneyverse.grpc.lib.UserRequest;
import it.moneyverse.grpc.lib.UserResponse;
import it.moneyverse.grpc.lib.UserServiceGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserServiceGrpcClient implements UserServiceClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceGrpcClient.class);

  private final UserServiceGrpc.UserServiceBlockingStub stub;

  public UserServiceGrpcClient(UserServiceGrpc.UserServiceBlockingStub stub) {
    this.stub = stub;
  }

  @Override
  @CircuitBreaker(name = UserServiceGrpcCircuitBreakerProperties.USER_SERVICE_GRPC, fallbackMethod = "fallbackCheckIfUserExists")
  public Boolean checkIfUserExists(String username) {
    final UserResponse response =
        stub.checkIfUserExists(UserRequest.newBuilder().setUsername(username).build());
    return response.getExists();
  }

  protected Boolean fallbackCheckIfUserExists(String username, Throwable throwable) {
    LOGGER.error("Impossible to contact the UserService to check whether the user {} exists. Returning FALSE as fallback: {}", username, throwable.getMessage());
    return false;
  }
}
