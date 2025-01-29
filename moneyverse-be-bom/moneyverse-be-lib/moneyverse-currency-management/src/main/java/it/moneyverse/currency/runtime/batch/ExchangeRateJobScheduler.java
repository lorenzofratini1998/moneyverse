package it.moneyverse.currency.runtime.batch;

import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ExchangeRateJobScheduler {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeRateJobScheduler.class);

  private final Job currencyRateJob;
  private final JobLauncher jobLauncher;

  public ExchangeRateJobScheduler(Job currencyRateJob, JobLauncher jobLauncher) {
    this.currencyRateJob = currencyRateJob;
    this.jobLauncher = jobLauncher;
  }

  @Scheduled(cron = "0 0 17 * * *")
  public void scheduleExchangeRateJob()
      throws JobInstanceAlreadyCompleteException,
          JobExecutionAlreadyRunningException,
          JobParametersInvalidException,
          JobRestartException {
    LOGGER.info("Starting ExchangeRateJob at {}", new Date());
    jobLauncher.run(
        currencyRateJob,
        new JobParametersBuilder()
            .addString("JobID", String.valueOf(System.currentTimeMillis()))
            .toJobParameters());
  }
}
