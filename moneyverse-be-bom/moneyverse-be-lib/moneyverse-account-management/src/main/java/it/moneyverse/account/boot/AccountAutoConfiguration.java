package it.moneyverse.account.boot;

import it.moneyverse.core.exceptions.MoneyverseExceptionHandler;
import it.moneyverse.core.model.beans.AccountDeletionTopic;
import it.moneyverse.core.model.beans.UserDeletionTopic;
import it.moneyverse.core.runtime.interceptor.LocaleInterceptor;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableJpaRepositories(
    basePackages = {
      "it.moneyverse.core.model.repositories",
      "it.moneyverse.account.model.repositories"
    })
@EntityScan(
    basePackages = {"it.moneyverse.core.model.entities", "it.moneyverse.account.model.entities"})
@Import({MoneyverseExceptionHandler.class, LocaleInterceptor.class})
public class AccountAutoConfiguration implements WebMvcConfigurer {

  private final LocaleInterceptor localeInterceptor;

  public AccountAutoConfiguration(LocaleInterceptor localeInterceptor) {
    this.localeInterceptor = localeInterceptor;
  }

  @Bean
  public AccountDeletionTopic accountDeletionTopic() {
    return new AccountDeletionTopic();
  }

  @Bean
  public UserDeletionTopic userDeletionTopic() {
    return new UserDeletionTopic();
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(localeInterceptor);
  }
}
