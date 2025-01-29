package it.moneyverse.currency.runtime.batch;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

@ExtendWith(MockitoExtension.class)
class ExchangeRateJobSchedulerTest {

  @Mock private Job currencyRateJob;
  @Mock private JobLauncher jobLauncher;

  @InjectMocks private ExchangeRateJobScheduler exchangeRateJobScheduler;

  @Test
  void testSchedulerExchangeRateJob()
      throws JobInstanceAlreadyCompleteException,
          JobExecutionAlreadyRunningException,
          JobParametersInvalidException,
          JobRestartException {
    exchangeRateJobScheduler.scheduleExchangeRateJob();
    verify(jobLauncher, times(1)).run(eq(currencyRateJob), any(JobParameters.class));
  }
}
