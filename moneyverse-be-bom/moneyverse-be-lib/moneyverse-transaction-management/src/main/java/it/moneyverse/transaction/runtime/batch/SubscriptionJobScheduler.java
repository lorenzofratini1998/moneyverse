package it.moneyverse.transaction.runtime.batch;

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
public class SubscriptionJobScheduler {

  private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionJobScheduler.class);

  private final Job subscriptionJob;
  private final JobLauncher jobLauncher;

  public SubscriptionJobScheduler(Job subscriptionJob, JobLauncher jobLauncher) {
    this.subscriptionJob = subscriptionJob;
    this.jobLauncher = jobLauncher;
  }

  @Scheduled(cron = "0 0 3 * * *")
  public void scheduleSubscriptionJob()
      throws JobInstanceAlreadyCompleteException,
          JobExecutionAlreadyRunningException,
          JobParametersInvalidException,
          JobRestartException {
    LOGGER.info("Starting SubscriptionJob at {}", new Date());
    jobLauncher.run(
        subscriptionJob,
        new JobParametersBuilder()
            .addString("JobID", String.valueOf(System.currentTimeMillis()))
            .toJobParameters());
  }
}
