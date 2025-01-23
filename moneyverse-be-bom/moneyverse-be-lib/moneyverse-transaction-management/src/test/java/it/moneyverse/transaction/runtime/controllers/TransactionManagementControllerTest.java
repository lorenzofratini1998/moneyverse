package it.moneyverse.transaction.runtime.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.moneyverse.core.boot.AccountServiceGrpcClientAutoConfiguration;
import it.moneyverse.core.boot.BudgetServiceGrpcClientAutoConfiguration;
import it.moneyverse.core.boot.KafkaAutoConfiguration;
import it.moneyverse.core.enums.CurrencyEnum;
import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.test.runtime.processor.MockAdminRequestPostProcessor;
import it.moneyverse.test.runtime.processor.MockUserRequestPostProcessor;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.transaction.model.dto.TransactionCriteria;
import it.moneyverse.transaction.model.dto.TransactionDto;
import it.moneyverse.transaction.model.dto.TransactionRequestDto;
import it.moneyverse.transaction.services.TransactionManagementService;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(
    controllers = {TransactionManagementController.class},
    excludeAutoConfiguration = {
      DataSourceAutoConfiguration.class,
      AccountServiceGrpcClientAutoConfiguration.class,
      BudgetServiceGrpcClientAutoConfiguration.class,
      KafkaAutoConfiguration.class
    })
@ExtendWith(MockitoExtension.class)
class TransactionManagementControllerTest {

  @Value("${spring.security.base-path}")
  String basePath;

  @Autowired private MockMvc mockMvc;
  @MockitoBean private TransactionManagementService transactionService;

  @Test
  void testCreateAccount_Success(@Mock TransactionDto response) throws Exception {
    String username = RandomUtils.randomString(15);
    TransactionRequestDto request = createTransactionRequest(username);

    when(transactionService.createTransaction(request)).thenReturn(response);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(basePath + "/transactions")
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockUserRequestPostProcessor.mockUser(username)))
        .andExpect(status().isCreated());
  }

  @Test
  void testCreateAccount_Forbidden() throws Exception {
    TransactionRequestDto request = createTransactionRequest(RandomUtils.randomString(15));

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(basePath + "/transactions")
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.anonymous()))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @MethodSource("invalidTransactionRequestProvider")
  void testCreateAccount_BadRequest(Supplier<TransactionRequestDto> requestSupplier)
      throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(basePath + "/transactions")
                .content(requestSupplier.get().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockUserRequestPostProcessor.mockUser(RandomUtils.randomString(15))))
        .andExpect(status().isBadRequest());
  }

  private static Stream<Supplier<TransactionRequestDto>> invalidTransactionRequestProvider() {
    return Stream.of(
        TransactionManagementControllerTest::createRequestWithNullUsername,
        TransactionManagementControllerTest::createRequestWithNullAccountId,
        TransactionManagementControllerTest::createRequestWithNullBudgetId,
        TransactionManagementControllerTest::createRequestWithNullDate,
        TransactionManagementControllerTest::createRequestWithNullAmount,
        TransactionManagementControllerTest::createRequestWithNullCurrency);
  }

  private static TransactionRequestDto createRequestWithNullUsername() {
    return new TransactionRequestDto(
        null,
        RandomUtils.randomUUID(),
        RandomUtils.randomUUID(),
        RandomUtils.randomLocalDate(2024, 2025),
        RandomUtils.randomString(15),
        RandomUtils.randomBigDecimal(),
        RandomUtils.randomEnum(CurrencyEnum.class),
        null);
  }

  private static TransactionRequestDto createRequestWithNullAccountId() {
    return new TransactionRequestDto(
        RandomUtils.randomString(15),
        null,
        RandomUtils.randomUUID(),
        RandomUtils.randomLocalDate(2024, 2025),
        RandomUtils.randomString(15),
        RandomUtils.randomBigDecimal(),
        RandomUtils.randomEnum(CurrencyEnum.class),
        null);
  }

  private static TransactionRequestDto createRequestWithNullBudgetId() {
    return new TransactionRequestDto(
        RandomUtils.randomString(15),
        RandomUtils.randomUUID(),
        null,
        RandomUtils.randomLocalDate(2024, 2025),
        RandomUtils.randomString(15),
        RandomUtils.randomBigDecimal(),
        RandomUtils.randomEnum(CurrencyEnum.class),
        null);
  }

  private static TransactionRequestDto createRequestWithNullDate() {
    return new TransactionRequestDto(
        RandomUtils.randomString(15),
        RandomUtils.randomUUID(),
        RandomUtils.randomUUID(),
        null,
        RandomUtils.randomString(15),
        RandomUtils.randomBigDecimal(),
        RandomUtils.randomEnum(CurrencyEnum.class),
        null);
  }

  private static TransactionRequestDto createRequestWithNullAmount() {
    return new TransactionRequestDto(
        RandomUtils.randomString(15),
        RandomUtils.randomUUID(),
        RandomUtils.randomUUID(),
        RandomUtils.randomLocalDate(2024, 2025),
        RandomUtils.randomString(15),
        null,
        RandomUtils.randomEnum(CurrencyEnum.class),
        null);
  }

  private static TransactionRequestDto createRequestWithNullCurrency() {
    return new TransactionRequestDto(
        RandomUtils.randomString(15),
        RandomUtils.randomUUID(),
        RandomUtils.randomUUID(),
        RandomUtils.randomLocalDate(2024, 2025),
        RandomUtils.randomString(15),
        RandomUtils.randomBigDecimal(),
        null,
        null);
  }

  @Test
  void testGetTransactions_Success(
      @Mock TransactionCriteria criteria, @Mock List<TransactionDto> response) throws Exception {
    when(transactionService.getTransactions(criteria)).thenReturn(response);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get(basePath + "/transactions")
                .with(MockAdminRequestPostProcessor.mockAdmin()))
        .andExpect(status().isOk());
  }

  @Test
  void testGetTransactions_Unauthorized() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(basePath + "/transactions")
                .with(SecurityMockMvcRequestPostProcessors.anonymous()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void testGetTransaction_Success(@Mock TransactionDto response) throws Exception {
    UUID transactionId = RandomUtils.randomUUID();
    when(transactionService.getTransaction(transactionId)).thenReturn(response);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get(basePath + "/transactions/" + transactionId)
                .with(MockAdminRequestPostProcessor.mockAdmin()))
        .andExpect(status().isOk());
  }

  @Test
  void testGetTransaction_NotFound() throws Exception {
    UUID transactionId = RandomUtils.randomUUID();
    when(transactionService.getTransaction(transactionId))
        .thenThrow(ResourceNotFoundException.class);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get(basePath + "/transactions/" + transactionId)
                .with(MockAdminRequestPostProcessor.mockAdmin()))
        .andExpect(status().isNotFound());
  }

  @Test
  void testGetTransaction_Unauthorized() throws Exception {
    UUID transactionId = RandomUtils.randomUUID();

    mockMvc
        .perform(
            MockMvcRequestBuilders.get(basePath + "/transactions/" + transactionId)
                .with(SecurityMockMvcRequestPostProcessors.anonymous()))
        .andExpect(status().isUnauthorized());
  }

  private TransactionRequestDto createTransactionRequest(String username) {
    Long tagId = RandomUtils.randomLong();
    return new TransactionRequestDto(
        username,
        RandomUtils.randomUUID(),
        RandomUtils.randomUUID(),
        RandomUtils.randomLocalDate(2024, 2025),
        RandomUtils.randomString(15),
        RandomUtils.randomBigDecimal(),
        RandomUtils.randomEnum(CurrencyEnum.class),
        Collections.singleton(tagId));
  }
}
