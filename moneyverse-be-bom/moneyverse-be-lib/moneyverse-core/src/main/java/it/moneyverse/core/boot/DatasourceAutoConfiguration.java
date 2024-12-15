package it.moneyverse.core.boot;

import it.moneyverse.core.utils.properties.DatasourceProperties;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(DatasourceProperties.class)
public class DatasourceAutoConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(DatasourceAutoConfiguration.class);

  private final DatasourceProperties properties;

  public DatasourceAutoConfiguration(DatasourceProperties properties) {
    this.properties = properties;
    LOGGER.info("Starting to load beans from {}", DatasourceAutoConfiguration.class.getName());
  }

  @Bean
  public DataSource dataSource() {
    return DataSourceBuilder.create()
        .driverClassName(properties.getDriverClassName())
        .url(properties.getUrl())
        .username(properties.getUsername())
        .password(properties.getPassword())
        .build();
  }
}
