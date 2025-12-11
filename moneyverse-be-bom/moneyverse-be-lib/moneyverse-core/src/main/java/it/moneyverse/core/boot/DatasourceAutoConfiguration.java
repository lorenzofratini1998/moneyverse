package it.moneyverse.core.boot;

import it.moneyverse.core.utils.properties.ClickhouseProperties;
import it.moneyverse.core.utils.properties.DatasourceProperties;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableConfigurationProperties({DatasourceProperties.class, ClickhouseProperties.class})
public class DatasourceAutoConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(DatasourceAutoConfiguration.class);

  private final DatasourceProperties properties;
  private final ClickhouseProperties clickhouseProperties;

  public DatasourceAutoConfiguration(
      DatasourceProperties properties, ClickhouseProperties clickhouseProperties) {
    this.properties = properties;
    this.clickhouseProperties = clickhouseProperties;
    LOGGER.info("Starting to load beans from {}", DatasourceAutoConfiguration.class.getName());
  }

  @Bean
  @Primary
  public DataSource dataSource() {
    return DataSourceBuilder.create()
        .driverClassName(properties.getDriverClassName())
        .url(properties.getUrl())
        .username(properties.getUsername())
        .password(properties.getPassword())
        .build();
  }

  @Bean
  @ConditionalOnProperty(prefix = ClickhouseProperties.PREFIX, name = "url")
  public DataSource clickhouseDataSource() {
    return DataSourceBuilder.create()
        .driverClassName(clickhouseProperties.getDriverClassName())
        .url(clickhouseProperties.getUrl())
        .username(clickhouseProperties.getUsername())
        .password(clickhouseProperties.getPassword())
        .build();
  }
}
