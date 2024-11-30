package it.moneyverse.account;

import it.moneyverse.core.boot.DatasourceAutoConfiguration;
import it.moneyverse.core.boot.SecurityAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({
    DatasourceAutoConfiguration.class,
    SecurityAutoConfiguration.class,
})
public class AccountManagementApplication {

  public static void main(String[] args) {
    SpringApplication.run(AccountManagementApplication.class, args);
  }
}