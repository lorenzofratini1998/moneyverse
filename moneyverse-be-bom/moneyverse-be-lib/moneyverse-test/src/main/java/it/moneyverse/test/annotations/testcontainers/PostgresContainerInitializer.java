package it.moneyverse.test.annotations.testcontainers;

import it.moneyverse.test.extensions.testcontainers.PostgresContainer;
import it.moneyverse.test.utils.constants.DatasourcePropertiesConstants;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.NonNull;

public class PostgresContainerInitializer implements
    ApplicationContextInitializer<ConfigurableApplicationContext> {

  static PostgresContainer postgresContainer = new PostgresContainer();

  static {
    postgresContainer.start();
  }

  @Override
  public void initialize(@NonNull ConfigurableApplicationContext applicationContext) {
    TestPropertyValues.of(
        Property.builder().withKey(DatasourcePropertiesConstants.DRIVER_CLASS_NAME)
            .withValue(postgresContainer.getDriverClassName()).build().toString(),
        Property.builder().withKey(DatasourcePropertiesConstants.URL)
            .withValue(postgresContainer.getJdbcUrl()).build().toString(),
        Property.builder().withKey(DatasourcePropertiesConstants.USERNAME)
            .withValue(postgresContainer.getUsername()).build().toString(),
        Property.builder().withKey(DatasourcePropertiesConstants.PASSWORD)
            .withValue(postgresContainer.getPassword()).build().toString()
        ).applyTo(applicationContext.getEnvironment());
  }
}
