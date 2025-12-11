package it.moneyverse.transaction.boot;

import it.moneyverse.core.exceptions.MoneyverseExceptionHandler;
import it.moneyverse.transaction.model.entities.Subscription;
import it.moneyverse.transaction.runtime.batch.SubscriptionProcessor;
import it.moneyverse.transaction.runtime.batch.SubscriptionReader;
import it.moneyverse.transaction.runtime.batch.SubscriptionWriter;
import java.util.List;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableScheduling
@EnableAsync
@EntityScan(
    basePackages = {
      "it.moneyverse.core.model.entities",
      "it.moneyverse.transaction.model.entities"
    })
@EnableJpaRepositories(
    basePackages = {
      "it.moneyverse.core.model.repositories",
      "it.moneyverse.transaction.model.repositories"
    })
@Import(MoneyverseExceptionHandler.class)
public class TransactionAutoConfiguration {

  @Bean
  public Step step1(
      JobRepository jobRepository,
      PlatformTransactionManager platformTransactionManager,
      SubscriptionReader subscriptionReader,
      SubscriptionProcessor subscriptionProcessor,
      SubscriptionWriter subscriptionWriter) {
    return new StepBuilder("subscriptionStep", jobRepository)
        .<List<Subscription>, List<Subscription>>chunk(100, platformTransactionManager)
        .reader(subscriptionReader)
        .processor(subscriptionProcessor)
        .writer(subscriptionWriter)
        .build();
  }

  @Bean
  public Job subscriptionJob(Step step1, JobRepository jobRepository) {
    return new JobBuilder("subscriptionJob", jobRepository).start(step1).build();
  }
}
