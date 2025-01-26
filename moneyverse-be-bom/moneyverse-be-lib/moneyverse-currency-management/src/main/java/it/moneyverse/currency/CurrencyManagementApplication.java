package it.moneyverse.currency;

import it.moneyverse.core.boot.AccountServiceGrpcClientAutoConfiguration;
import it.moneyverse.core.boot.BudgetServiceGrpcClientAutoConfiguration;
import it.moneyverse.core.boot.UserServiceGrpcClientAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
    exclude = {
      AccountServiceGrpcClientAutoConfiguration.class,
      BudgetServiceGrpcClientAutoConfiguration.class,
      UserServiceGrpcClientAutoConfiguration.class
    })
public class CurrencyManagementApplication {

  public static void main(String[] args) {
    SpringApplication.run(CurrencyManagementApplication.class, args);
  }
}
