package it.moneyverse.core.utils.properties;

import java.util.Objects;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = RedisProperties.PREFIX)
public class RedisProperties {

  public static final String PREFIX = "spring.cache.redis";
  public static final String HOST = PREFIX + ".host";
  public static final String PORT = PREFIX + ".port";
  public static final String PASSWORD = PREFIX + ".password";
  public static final String CONNECTION_TIMEOUT = PREFIX + ".connection-timeout";

  private final String host;
  private final Integer port;
  private final String password;
  private final Integer connectionTimeout;

  @ConstructorBinding
  public RedisProperties(
      String host, Integer port, String username, String password, Integer connectionTimeout) {
    this.host = Objects.requireNonNull(host, HOST + " is required");
    this.port = Objects.requireNonNull(port, PORT + " is required");
    this.password = Objects.requireNonNull(password, PASSWORD + " is required");
    this.connectionTimeout = connectionTimeout != null ? connectionTimeout : 60;
  }

  public String getHost() {
    return host;
  }

  public Integer getPort() {
    return port;
  }

  public String getPassword() {
    return password;
  }

  public Integer getConnectionTimeout() {
    return connectionTimeout;
  }
}
