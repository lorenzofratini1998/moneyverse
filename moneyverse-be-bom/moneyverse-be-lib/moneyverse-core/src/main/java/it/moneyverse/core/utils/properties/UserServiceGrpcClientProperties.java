package it.moneyverse.core.utils.properties;

import jakarta.annotation.PostConstruct;
import java.util.Objects;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = UserServiceGrpcClientProperties.USER_SERVICE_CLIENT_PREFIX)
public class UserServiceGrpcClientProperties {

  public static final String USER_SERVICE_CLIENT_PREFIX = "grpc.client.user-service";
  public static final String USER_SERVICE_CLIENT_HOST = "grpc.client.user-service.host";
  public static final String USER_SERVICE_CLIENT_PORT = "grpc.client.user-service.port";

  private final String host;
  private final Integer port;

  @ConstructorBinding
  public UserServiceGrpcClientProperties(String host, Integer port) {
    this.host = host;
    this.port = port;
  }

  @PostConstruct
  public void init() {
    Objects.requireNonNull(
        host, UserServiceGrpcClientProperties.USER_SERVICE_CLIENT_HOST + " is required");
    Objects.requireNonNull(
        port, UserServiceGrpcClientProperties.USER_SERVICE_CLIENT_PORT + " is required");
  }

  public String getHost() {
    return host;
  }

  public Integer getPort() {
    return port;
  }
}
