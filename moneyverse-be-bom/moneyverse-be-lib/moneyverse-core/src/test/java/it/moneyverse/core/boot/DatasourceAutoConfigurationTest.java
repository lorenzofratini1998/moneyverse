package it.moneyverse.core.boot;

import static org.assertj.core.api.Assertions.assertThat;

import it.moneyverse.core.utils.properties.DatasourceProperties;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class DatasourceAutoConfigurationTest {

  private final ApplicationContextRunner applicationContextRunner =
      new ApplicationContextRunner()
          .withUserConfiguration(DatasourceAutoConfiguration.class)
          .withPropertyValues(
              "%s=%s".formatted(DatasourceProperties.DRIVER_CLASS_NAME, "org.h2.Driver"),
              "%s=%s".formatted(DatasourceProperties.URL, "jdbc:h2:mem:test"),
              "%s=%s".formatted(DatasourceProperties.USERNAME, "sa"),
              "%s=%s".formatted(DatasourceProperties.PASSWORD, ""));

  @Test
  void testDatasourceBeanIsCreated() {
    applicationContextRunner.run(
        applicationContext -> {
          assertThat(applicationContext).hasSingleBean(DataSource.class);
          assertThat(applicationContext).hasSingleBean(DatasourceProperties.class);
        });
  }
}
