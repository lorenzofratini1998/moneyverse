package it.moneyverse.core.utils.properties;

import jakarta.annotation.PostConstruct;
import java.util.Objects;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = DatasourceProperties.PREFIX)
public class DatasourceProperties {

  public static final String PREFIX = "spring.datasource";
  public static final String DRIVER_CLASS_NAME = PREFIX + ".driver-class-name";
  public static final String URL = PREFIX + ".url";
  public static final String USERNAME = PREFIX + ".username";
  public static final String PASSWORD = PREFIX + ".password";

  private final String driverClassName;
  private final String url;
  private final String username;
  private final String password;

  @ConstructorBinding
  public DatasourceProperties(
      String driverClassName, String url, String username, String password) {
    this.driverClassName = driverClassName;
    this.url = url;
    this.username = username;
    this.password = password;
  }

  @PostConstruct
  public void init() {
    Objects.requireNonNull(
        driverClassName, DatasourceProperties.DRIVER_CLASS_NAME + " is required.");
    Objects.requireNonNull(url, DatasourceProperties.URL + " is required");
    Objects.requireNonNull(username, DatasourceProperties.USERNAME + " is required");
    Objects.requireNonNull(password, DatasourceProperties.PASSWORD + " is required");
  }

  public String getDriverClassName() {
    return driverClassName;
  }

  public String getUrl() {
    return url;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }
}
