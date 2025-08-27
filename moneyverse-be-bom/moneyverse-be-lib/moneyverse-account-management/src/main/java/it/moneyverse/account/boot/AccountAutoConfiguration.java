package it.moneyverse.account.boot;

import it.moneyverse.core.model.beans.AccountDeletionTopic;
import it.moneyverse.core.model.beans.UserDeletionTopic;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
    basePackages = {
      "it.moneyverse.core.model.repositories",
      "it.moneyverse.account.model.repositories"
    })
@EntityScan(
    basePackages = {"it.moneyverse.core.model.entities", "it.moneyverse.account.model.entities"})
public class AccountAutoConfiguration {

  @Bean
  public AccountDeletionTopic accountDeletionTopic() {
    return new AccountDeletionTopic();
  }

  @Bean
  public UserDeletionTopic userDeletionTopic() {
    return new UserDeletionTopic();
  }
}
