package it.moneyverse.analytics.boot;

import it.moneyverse.analytics.model.entities.TransactionEvent;
import it.moneyverse.analytics.model.entities.TransactionEventBuffer;
import it.moneyverse.analytics.runtime.batch.TransactionEventProcessor;
import it.moneyverse.analytics.runtime.batch.TransactionEventReader;
import it.moneyverse.analytics.runtime.batch.TransactionEventWriter;
import java.util.List;
import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableScheduling
public class AnalyticsAutoConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(AnalyticsAutoConfiguration.class);

  public AnalyticsAutoConfiguration() {
    LOGGER.info("Starting to load beans from {}", AnalyticsAutoConfiguration.class.getName());
  }

  @Bean
  public Step step1(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      TransactionEventReader transactionEventReader,
      TransactionEventProcessor transactionEventProcessor,
      TransactionEventWriter transactionEventWriter) {
    return new StepBuilder("transactionEventStep", jobRepository)
        .<List<TransactionEventBuffer>, List<TransactionEvent>>chunk(1, transactionManager)
        .reader(transactionEventReader)
        .processor(transactionEventProcessor)
        .writer(transactionEventWriter)
        .build();
  }

  @Bean
  public Job transactionEventJob(Step step1, JobRepository jobRepository) {
    return new JobBuilder("transactionEventJob", jobRepository).start(step1).build();
  }
}
