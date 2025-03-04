package it.moneyverse.budget;

import it.moneyverse.core.boot.AccountServiceGrpcClientAutoConfiguration;
import it.moneyverse.core.boot.BudgetServiceGrpcClientAutoConfiguration;
import it.moneyverse.core.exceptions.MoneyverseExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication(
    exclude = {
      AccountServiceGrpcClientAutoConfiguration.class,
      BudgetServiceGrpcClientAutoConfiguration.class
    })
@Import(MoneyverseExceptionHandler.class)
public class BudgetManagementApplication {

  public static void main(String[] args) {
    SpringApplication.run(BudgetManagementApplication.class, args);
  }
}
