package it.moneyverse.user;

import it.moneyverse.core.boot.*;
import it.moneyverse.core.exceptions.MoneyverseExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication(
    exclude = {
      UserServiceGrpcClientAutoConfiguration.class,
      AccountServiceGrpcClientAutoConfiguration.class,
      BudgetServiceGrpcClientAutoConfiguration.class,
      RedisAutoConfiguration.class,
      OutboxAutoConfiguration.class
    })
@Import(MoneyverseExceptionHandler.class)
public class UserManagementApplication {

  public static void main(String[] args) {
    SpringApplication.run(UserManagementApplication.class, args);
  }
}
