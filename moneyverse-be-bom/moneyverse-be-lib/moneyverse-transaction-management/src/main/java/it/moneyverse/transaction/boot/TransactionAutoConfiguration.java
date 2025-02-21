package it.moneyverse.transaction.boot;

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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableScheduling
@EnableAsync
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
