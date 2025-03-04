package it.moneyverse.currency.runtime.batch;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import it.moneyverse.currency.model.CurrencyTestContext;
import it.moneyverse.currency.model.repositories.ExchangeRateRepository;
import it.moneyverse.test.annotations.MoneyverseTest;
import it.moneyverse.test.annotations.datasource.FlywayTestDir;
import it.moneyverse.test.extensions.testcontainers.PostgresContainer;
import it.moneyverse.test.operations.mapping.EntityScriptGenerator;
import it.moneyverse.test.utils.properties.TestPropertyRegistry;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.*;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.junit.jupiter.Container;

@MoneyverseTest
@SpringBatchTest
@TestPropertySource(
    properties = {
      "spring.autoconfigure.exclude=it.moneyverse.core.boot.SecurityAutoConfiguration",
      "spring.runner.initializer.enabled=false"
    })
class ExchangeRateIT {

  protected static CurrencyTestContext testContext;

  @FlywayTestDir(fileName = EntityScriptGenerator.SQL_SCRIPT_FILE_NAME)
  protected static Path tempDir;

  @Container static PostgresContainer postgresContainer = new PostgresContainer();
  private final Resource resource = new ClassPathResource("files/ExchangeRates_Response.xml");

  @Autowired private JobLauncherTestUtils jobLauncherTestUtils;
  @Autowired private JobRepositoryTestUtils jobRepositoryTestUtils;
  @Autowired private ExchangeRateRepository exchangeRateRepository;
  @MockitoBean RestTemplate restTemplate;

  @DynamicPropertySource
  static void mappingProperties(DynamicPropertyRegistry registry) {
    new TestPropertyRegistry(registry)
        .withPostgres(postgresContainer)
        .withFlywayTestDirectory(tempDir);
  }

  @BeforeAll
  static void beforeAll() {
    testContext = new CurrencyTestContext().generateScript(tempDir);
  }

  @Test
  void testCurrencyRateJob() throws Exception {
    String xmlContent = new String(Files.readAllBytes(resource.getFile().toPath()));
    when(restTemplate.getForEntity(anyString(), eq(String.class)))
        .thenReturn(new ResponseEntity<>(xmlContent, HttpStatus.OK));

    JobParameters jobParameters =
        new JobParametersBuilder()
            .addString("JobID", String.valueOf(System.currentTimeMillis()))
            .toJobParameters();
    int initialSize = exchangeRateRepository.findAll().size();

    var jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

    Assertions.assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    Assertions.assertEquals(initialSize + 30, exchangeRateRepository.findAll().size());
  }
}
