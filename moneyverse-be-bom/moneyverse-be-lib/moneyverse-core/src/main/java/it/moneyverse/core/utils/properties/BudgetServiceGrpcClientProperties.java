package it.moneyverse.core.utils.properties;

import java.util.Objects;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = BudgetServiceGrpcClientProperties.BUDGET_SERVICE_CLIENT_PREFIX)
public class BudgetServiceGrpcClientProperties {

  public static final String BUDGET_SERVICE_CLIENT_PREFIX = "grpc.client.budget-service";
  public static final String BUDGET_SERVICE_CLIENT_HOST = BUDGET_SERVICE_CLIENT_PREFIX + ".host";
  public static final String BUDGET_SERVICE_CLIENT_PORT = BUDGET_SERVICE_CLIENT_PREFIX + ".port";

  private final String host;
  private final Integer port;

  @ConstructorBinding
  public BudgetServiceGrpcClientProperties(String host, Integer port) {
    this.host =
        Objects.requireNonNull(
            host, BudgetServiceGrpcClientProperties.BUDGET_SERVICE_CLIENT_HOST + " is required");
    this.port =
        Objects.requireNonNull(
            port, BudgetServiceGrpcClientProperties.BUDGET_SERVICE_CLIENT_PORT + " is required");
  }

  public String getHost() {
    return host;
  }

  public Integer getPort() {
    return port;
  }
}
