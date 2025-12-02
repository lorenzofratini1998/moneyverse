package it.moneyverse.analytics.boot;

import jakarta.persistence.EntityManagerFactory;
import java.util.Map;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = {
      "it.moneyverse.analytics.model.repositories",
      "it.moneyverse.core.model.repositories"
    })
@EntityScan(
    basePackages = {"it.moneyverse.analytics.model.entities", "it.moneyverse.core.model.entities"})
public class PostgresAutoConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(PostgresAutoConfiguration.class);

  public PostgresAutoConfiguration() {
    LOGGER.info("Starting to load beans from {}", PostgresAutoConfiguration.class.getName());
  }

  @Bean
  public Flyway postgresFlyway(
      @Qualifier("dataSource") DataSource dataSource,
      @Value("${spring.flyway.postgres.locations}") String locations,
      @Value("#{${spring.flyway.postgres.placeholders:{}}}") Map<String, String> placeholders) {

    FluentConfiguration configuration =
        Flyway.configure()
            .dataSource(dataSource)
            .locations(locations.split("\\s*,\\s*"))
            .baselineOnMigrate(true)
            .cleanDisabled(false);

    if (placeholders != null && !placeholders.isEmpty()) {
      configuration.placeholders(placeholders);
    }

    Flyway flyway = configuration.load();
    flyway.migrate();
    return flyway;
  }

  @Bean
  public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
    return new JpaTransactionManager(emf);
  }
}
