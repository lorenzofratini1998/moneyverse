package it.moneyverse.transaction;

import it.moneyverse.core.exceptions.MoneyverseExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(MoneyverseExceptionHandler.class)
public class TransactionManagementApplication {

  public static void main(String[] args) {
    SpringApplication.run(TransactionManagementApplication.class, args);
  }
}
