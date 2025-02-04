package it.moneyverse.user;

import it.moneyverse.core.boot.AccountServiceGrpcClientAutoConfiguration;
import it.moneyverse.core.boot.BudgetServiceGrpcClientAutoConfiguration;
import it.moneyverse.core.boot.UserServiceGrpcClientAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
    exclude = {
      UserServiceGrpcClientAutoConfiguration.class,
      AccountServiceGrpcClientAutoConfiguration.class,
      BudgetServiceGrpcClientAutoConfiguration.class,
    })
public class UserManagementApplication {

  public static void main(String[] args) {
    SpringApplication.run(UserManagementApplication.class, args);
  }
}
