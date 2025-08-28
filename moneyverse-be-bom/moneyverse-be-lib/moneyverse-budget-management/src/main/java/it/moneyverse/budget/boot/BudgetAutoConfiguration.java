package it.moneyverse.budget.boot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.moneyverse.core.model.beans.*;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(
    basePackages = {"it.moneyverse.budget.model.entities", "it.moneyverse.core.model.entities"})
@EnableJpaRepositories(
    basePackages = {
      "it.moneyverse.budget.model.repositories",
      "it.moneyverse.core.model.repositories"
    })
public class BudgetAutoConfiguration {

  @Bean
  public CategoryDeletionTopic categoryDeletionTopic() {
    return new CategoryDeletionTopic();
  }

  @Bean
  public BudgetDeletionTopic budgetDeletionTopic() {
    return new BudgetDeletionTopic();
  }

  @Bean
  public UserDeletionTopic userDeletionTopic() {
    return new UserDeletionTopic();
  }

  @Bean
  public UserCreationTopic userCreationTopic() {
    return new UserCreationTopic();
  }

  @Bean
  public TransactionCreationTopic transactionCreationTopic() {
    return new TransactionCreationTopic();
  }

  @Bean
  public TransactionUpdateTopic transactionUpdateTopic() {
    return new TransactionUpdateTopic();
  }

  @Bean
  public TransactionDeletionTopic transactionDeletionTopic() {
    return new TransactionDeletionTopic();
  }

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .registerModule(new JsonNullableModule());
  }
}
