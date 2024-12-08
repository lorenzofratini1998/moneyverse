package it.moneyverse.account.boot;

import it.moneyverse.core.model.beans.AccountDeletionTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountAutoConfiguration {

  @Bean
  public AccountDeletionTopic accountDeletionTopic() {
    return new AccountDeletionTopic();
  }
}
