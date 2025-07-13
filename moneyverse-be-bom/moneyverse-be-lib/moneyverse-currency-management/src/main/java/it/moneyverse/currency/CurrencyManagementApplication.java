package it.moneyverse.currency;

import it.moneyverse.core.boot.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
    exclude = {
      AccountServiceGrpcClientAutoConfiguration.class,
      BudgetServiceGrpcClientAutoConfiguration.class,
      UserServiceGrpcClientAutoConfiguration.class,
      CurrencyServiceGrpcClientAutoConfiguration.class,
      KafkaAutoConfiguration.class,
      RedisAutoConfiguration.class,
      OutboxAutoConfiguration.class
    })
public class CurrencyManagementApplication {

  public static void main(String[] args) {
    SpringApplication.run(CurrencyManagementApplication.class, args);
  }
}
