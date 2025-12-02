package it.moneyverse.test.annotations.datasource;

import java.util.Map;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class FlywayTestExtension implements BeforeEachCallback {

  @Override
  public void beforeEach(ExtensionContext context) {
    ApplicationContext applicationContext = SpringExtension.getApplicationContext(context);
    Map<String, Flyway> flywayBeans = applicationContext.getBeansOfType(Flyway.class);
    if (flywayBeans.isEmpty()) {
      throw new RuntimeException("No Flyway beans found in application context");
    }
    flywayBeans.forEach(
        (beanName, flyway) -> {
          try {
            flyway.clean();
            flyway.migrate();
          } catch (Exception e) {
            throw new RuntimeException("Failed to process Flyway bean: " + beanName, e);
          }
        });
  }
}
