package it.moneyverse.core.utils.properties;

import java.util.Objects;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = AccountServiceGrpcClientProperties.ACCOUNT_SERVICE_CLIENT_PREFIX)
public class AccountServiceGrpcClientProperties {

  public static final String ACCOUNT_SERVICE_CLIENT_PREFIX = "grpc.client.account-service";
  public static final String ACCOUNT_SERVICE_CLIENT_HOST = ACCOUNT_SERVICE_CLIENT_PREFIX + ".host";
  public static final String ACCOUNT_SERVICE_CLIENT_PORT = ACCOUNT_SERVICE_CLIENT_PREFIX + ".port";

  private final String host;
  private final Integer port;

  @ConstructorBinding
  public AccountServiceGrpcClientProperties(String host, Integer port) {
    this.host =
        Objects.requireNonNull(
            host, AccountServiceGrpcClientProperties.ACCOUNT_SERVICE_CLIENT_HOST + " is required");
    this.port =
        Objects.requireNonNull(
            port, AccountServiceGrpcClientProperties.ACCOUNT_SERVICE_CLIENT_PORT + " is required");
  }

  public String getHost() {
    return host;
  }

  public Integer getPort() {
    return port;
  }
}
