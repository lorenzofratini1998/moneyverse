package it.moneyverse.account;

import it.moneyverse.core.boot.DatasourceAutoConfiguration;
import it.moneyverse.core.boot.SecurityAutoConfiguration;
import it.moneyverse.core.boot.UserServiceGrpcClientAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@ImportAutoConfiguration({
  DatasourceAutoConfiguration.class,
  SecurityAutoConfiguration.class,
  UserServiceGrpcClientAutoConfiguration.class
})
public class AccountManagementApplication {

  public static void main(String[] args) {
    SpringApplication.run(AccountManagementApplication.class, args);
  }
}
