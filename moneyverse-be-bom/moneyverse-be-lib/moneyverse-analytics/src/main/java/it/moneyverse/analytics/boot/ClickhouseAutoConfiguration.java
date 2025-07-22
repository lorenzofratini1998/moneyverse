package it.moneyverse.analytics.boot;

import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Configuration
public class ClickhouseAutoConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClickhouseAutoConfiguration.class);

  @Bean
  public Flyway clickhouseFlyway(
      @Qualifier("clickhouseDataSource") DataSource dataSource,
      @Value("${spring.flyway.clickhouse.locations}") String locations) {
    Flyway flyway =
        Flyway.configure()
            .dataSource(dataSource)
            .locations(locations.split("\\s*,\\s*"))
            .baselineOnMigrate(true)
            .cleanDisabled(false)
            .load();
    flyway.migrate();
    return flyway;
  }

  @Bean
  public JdbcTemplate clickHouseJdbcTemplate(
      @Qualifier("clickhouseDataSource") DataSource dataSource) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    jdbcTemplate.setFetchSize(10000);
    jdbcTemplate.setQueryTimeout(300); // 5 minutes
    return jdbcTemplate;
  }

  @Bean
  public NamedParameterJdbcTemplate clickHouseNamedParameterJdbcTemplate(
      @Qualifier("clickHouseJdbcTemplate") JdbcTemplate jdbcTemplate) {
    return new NamedParameterJdbcTemplate(jdbcTemplate);
  }
}
