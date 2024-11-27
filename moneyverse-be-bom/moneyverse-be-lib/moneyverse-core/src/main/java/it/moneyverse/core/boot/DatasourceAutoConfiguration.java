package it.moneyverse.core.boot;

import jakarta.annotation.PostConstruct;
import java.util.Objects;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "datasource.config")
public class DatasourceAutoConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(DatasourceAutoConfiguration.class);

  private String driverClassName;
  private String url;
  private String username;
  private String password;

  public DatasourceAutoConfiguration() {
    LOGGER.info("Starting to load beans from {}", DatasourceAutoConfiguration.class.getName());
  }

  @PostConstruct
  public void init() {
    Objects.requireNonNull(driverClassName, "driverClassName is required");
    Objects.requireNonNull(url, "url is required");
    Objects.requireNonNull(username, "username is required");
    Objects.requireNonNull(password, "password is required");
  }

  @Bean
  public DataSource dataSource() {
    return DataSourceBuilder.create()
        .driverClassName(driverClassName)
        .url(url)
        .username(username)
        .password(password)
        .build();
  }

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
