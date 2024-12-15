package it.moneyverse.budget;

import it.moneyverse.core.boot.KafkaAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {KafkaAutoConfiguration.class})
public class BudgetManagementApplication {

  public static void main(String[] args) {
    SpringApplication.run(BudgetManagementApplication.class, args);
  }
}
