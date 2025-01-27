package it.moneyverse.currency.boot;

import it.moneyverse.currency.model.entities.ExchangeRate;
import it.moneyverse.currency.runtime.batch.ExchangeRateProcessor;
import it.moneyverse.currency.runtime.batch.ExchangeRateReader;
import it.moneyverse.currency.runtime.batch.ExchangeRateWriter;
import java.util.List;
import java.util.concurrent.Executor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableScheduling
@EnableAsync
public class CurrencyAutoConfiguration {

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  public Step step1(
      JobRepository jobRepository,
      PlatformTransactionManager platformTransactionManager,
      ExchangeRateReader exchangeRateReader,
      ExchangeRateProcessor exchangeRateProcessor,
      ExchangeRateWriter exchangeRateWriter) {
    return new StepBuilder("currencyRateStep", jobRepository)
        .<String, List<ExchangeRate>>chunk(1, platformTransactionManager)
        .reader(exchangeRateReader)
        .processor(exchangeRateProcessor)
        .writer(exchangeRateWriter)
        .build();
  }

  @Bean
  public Job currencyRateJob(Step step1, JobRepository jobRepository) {
    return new JobBuilder("currencyRateJob", jobRepository).start(step1).build();
  }

  @Bean
  public Executor asyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(2);
    executor.setMaxPoolSize(2);
    executor.setQueueCapacity(500);
    executor.setThreadNamePrefix("CurrencyAsync-");
    executor.initialize();
    return executor;
  }
}
