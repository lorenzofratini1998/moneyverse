package it.moneyverse.analytics.runtime.batch;

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
public class TransactionEventScheduler {
  private final Job transactionEventJob;
  private final JobLauncher jobLauncher;

  public TransactionEventScheduler(Job transactionEventJob, JobLauncher jobLauncher) {
    this.transactionEventJob = transactionEventJob;
    this.jobLauncher = jobLauncher;
  }

  @Scheduled(cron = "*/5 * * * * *")
  public void scheduleTransactionEventJob()
      throws JobInstanceAlreadyCompleteException,
          JobExecutionAlreadyRunningException,
          JobParametersInvalidException,
          JobRestartException {
    jobLauncher.run(
        transactionEventJob,
        new JobParametersBuilder()
            .addString("JobID", String.valueOf(System.currentTimeMillis()))
            .toJobParameters());
  }
}
