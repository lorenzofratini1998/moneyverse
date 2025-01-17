package it.moneyverse.transaction;

import it.moneyverse.core.boot.UserServiceGrpcClientAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {UserServiceGrpcClientAutoConfiguration.class})
public class TransactionManagementApplication {

  public static void main(String[] args) {
    SpringApplication.run(TransactionManagementApplication.class, args);
  }
}
