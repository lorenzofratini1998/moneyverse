package it.moneyverse.transaction;

import it.moneyverse.core.exceptions.MoneyverseExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(
    basePackages = {
      "it.moneyverse.core.model.entities",
      "it.moneyverse.transaction.model.entities"
    })
@EnableJpaRepositories(
    basePackages = {
      "it.moneyverse.core.model.repositories",
      "it.moneyverse.transaction.model.repositories"
    })
@Import(MoneyverseExceptionHandler.class)
public class TransactionManagementApplication {

  public static void main(String[] args) {
    SpringApplication.run(TransactionManagementApplication.class, args);
  }
}
