package it.moneyverse.analytics;

import it.moneyverse.core.boot.*;
import it.moneyverse.core.exceptions.MoneyverseExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication(
    exclude = {
      AccountServiceGrpcClientAutoConfiguration.class,
      BudgetServiceGrpcClientAutoConfiguration.class,
      UserServiceGrpcClientAutoConfiguration.class,
      CurrencyServiceGrpcClientAutoConfiguration.class,
      RedisAutoConfiguration.class,
      OutboxAutoConfiguration.class
    })
public class AnalyticsApplication {

  public static void main(String[] args) {
    SpringApplication.run(AnalyticsApplication.class, args);
  }
}
