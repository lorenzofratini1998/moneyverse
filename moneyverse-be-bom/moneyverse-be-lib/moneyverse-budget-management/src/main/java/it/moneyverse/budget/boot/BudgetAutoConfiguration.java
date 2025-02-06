package it.moneyverse.budget.boot;

import it.moneyverse.core.model.beans.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
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
}
