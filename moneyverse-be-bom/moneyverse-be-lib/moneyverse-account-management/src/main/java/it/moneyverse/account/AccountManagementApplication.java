package it.moneyverse.account;

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
    basePackages = {"it.moneyverse.core.model.entities", "it.moneyverse.account.model.entities"})
@EnableJpaRepositories(
    basePackages = {
      "it.moneyverse.core.model.repositories",
      "it.moneyverse.account.model.repositories"
    })
@Import(MoneyverseExceptionHandler.class)
public class AccountManagementApplication {

  public static void main(String[] args) {
    SpringApplication.run(AccountManagementApplication.class, args);
  }
}
