package it.moneyverse.core.utils.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = ClickhouseProperties.PREFIX)
public class ClickhouseProperties {

  public static final String PREFIX = "spring.datasource.clickhouse";
  public static final String DRIVER_CLASS_NAME = PREFIX + ".driver-class-name";
  public static final String URL = PREFIX + ".url";
  public static final String USERNAME = PREFIX + ".username";
  public static final String PASSWORD = PREFIX + ".password";

  private String driverClassName;
  private String url;
  private String username;
  private String password;

  public String getDriverClassName() {
    return driverClassName;
  }

  public void setDriverClassName(String driverClassName) {
    this.driverClassName = driverClassName;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
