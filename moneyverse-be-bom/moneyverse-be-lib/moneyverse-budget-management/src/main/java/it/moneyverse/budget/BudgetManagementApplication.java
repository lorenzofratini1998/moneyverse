package it.moneyverse.budget;

import it.moneyverse.core.boot.AccountServiceGrpcClientAutoConfiguration;
import it.moneyverse.core.boot.BudgetServiceGrpcClientAutoConfiguration;
import it.moneyverse.core.exceptions.MoneyverseExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
    exclude = {
      AccountServiceGrpcClientAutoConfiguration.class,
      BudgetServiceGrpcClientAutoConfiguration.class
    })
@EntityScan(
    basePackages = {"it.moneyverse.budget.model.entities", "it.moneyverse.core.model.entities"})
@EnableJpaRepositories(
    basePackages = {
      "it.moneyverse.budget.model.repositories",
      "it.moneyverse.core.model.repositories"
    })
@Import(MoneyverseExceptionHandler.class)
public class BudgetManagementApplication {

  public static void main(String[] args) {
    SpringApplication.run(BudgetManagementApplication.class, args);
  }
}
