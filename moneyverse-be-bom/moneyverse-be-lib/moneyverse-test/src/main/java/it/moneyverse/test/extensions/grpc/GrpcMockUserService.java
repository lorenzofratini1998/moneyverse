package it.moneyverse.test.extensions.grpc;

import static org.grpcmock.GrpcMock.stubFor;
import static org.grpcmock.GrpcMock.unaryMethod;

import it.moneyverse.core.utils.properties.UserServiceGrpcClientProperties;
import it.moneyverse.grpc.lib.UserResponse;
import it.moneyverse.grpc.lib.UserServiceGrpc;
import org.grpcmock.junit5.GrpcMockExtension;
import org.springframework.test.context.DynamicPropertyRegistry;

public class GrpcMockUserService extends GrpcMockExtension {

  private final int port;
  private final String host = "localhost";

  public GrpcMockUserService() {
    super(GrpcMockExtension.builder().withPort(0).build().getInstance());
    port = super.getPort();
  }

  public int getPort() {
    return port;
  }

  public String getHost() {
    return host;
  }

  public void setupProperties(DynamicPropertyRegistry registry) {
    registry.add(UserServiceGrpcClientProperties.USER_SERVICE_CLIENT_HOST, () -> host);
    registry.add(UserServiceGrpcClientProperties.USER_SERVICE_CLIENT_PORT, () -> port);
  }

  public void mockExistentUser() {
    stubFor(
        unaryMethod(UserServiceGrpc.getCheckIfUserExistsMethod())
            .willReturn(UserResponse.newBuilder().setExists(true).build()));
  }

  public void mockNonExistentUser() {
    stubFor(
        unaryMethod(UserServiceGrpc.getCheckIfUserExistsMethod())
            .willReturn(UserResponse.newBuilder().setExists(false).build()));
  }
}
