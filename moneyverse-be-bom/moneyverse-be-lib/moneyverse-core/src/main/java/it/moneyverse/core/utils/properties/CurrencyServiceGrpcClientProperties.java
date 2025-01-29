package it.moneyverse.core.utils.properties;

import java.util.Objects;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
    prefix = CurrencyServiceGrpcClientProperties.CURRENCY_SERVICE_CLIENT_PREFIX)
public class CurrencyServiceGrpcClientProperties {

  public static final String CURRENCY_SERVICE_CLIENT_PREFIX = "grpc.client.currency-service";
  public static final String CURRENCY_SERVICE_CLIENT_HOST =
      CURRENCY_SERVICE_CLIENT_PREFIX + ".host";
  public static final String CURRENCY_SERVICE_CLIENT_PORT =
      CURRENCY_SERVICE_CLIENT_PREFIX + ".port";

  private final String host;
  private final Integer port;

  public CurrencyServiceGrpcClientProperties(String host, Integer port) {
    this.host =
        Objects.requireNonNull(
            host,
            CurrencyServiceGrpcClientProperties.CURRENCY_SERVICE_CLIENT_HOST + " is required");
    this.port =
        Objects.requireNonNull(
            port,
            CurrencyServiceGrpcClientProperties.CURRENCY_SERVICE_CLIENT_PORT + " is required");
  }

  public String getHost() {
    return host;
  }

  public Integer getPort() {
    return port;
  }
}
