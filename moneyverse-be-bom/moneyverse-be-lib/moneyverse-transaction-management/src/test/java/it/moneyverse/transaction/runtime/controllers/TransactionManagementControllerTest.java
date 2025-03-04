package it.moneyverse.transaction.runtime.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.moneyverse.core.boot.AccountServiceGrpcClientAutoConfiguration;
import it.moneyverse.core.boot.BudgetServiceGrpcClientAutoConfiguration;
import it.moneyverse.core.boot.KafkaAutoConfiguration;
import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.test.runtime.processor.MockAdminRequestPostProcessor;
import it.moneyverse.test.runtime.processor.MockUserRequestPostProcessor;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.transaction.model.SubscriptionTestFactory;
import it.moneyverse.transaction.model.TransactionTestFactory;
import it.moneyverse.transaction.model.dto.*;
import it.moneyverse.transaction.services.SubscriptionService;
import it.moneyverse.transaction.services.TagManagementService;
import it.moneyverse.transaction.services.TransactionManagementService;
import it.moneyverse.transaction.services.TransferManagementService;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
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
  @MockitoBean private TransferManagementService transferService;
  @MockitoBean private TagManagementService tagService;
  @MockitoBean private SubscriptionService subscriptionService;

  @Test
  void testCreateAccount_Success(@Mock TransactionDto transactionDto) throws Exception {
    UUID userId = RandomUtils.randomUUID();
    TransactionRequestDto request =
        TransactionTestFactory.TransactionRequestBuilder.builder().withUserId(userId).build();

    when(transactionService.createTransactions(request)).thenReturn(List.of(transactionDto));

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(basePath + "/transactions")
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockUserRequestPostProcessor.mockUser(userId)))
        .andExpect(status().isCreated());
  }

  @Test
  void testCreateAccount_Forbidden() throws Exception {
    TransactionRequestDto request =
        TransactionTestFactory.TransactionRequestBuilder.defaultInstance();

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(basePath + "/transactions")
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.anonymous()))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @MethodSource(
      "it.moneyverse.transaction.model.TransactionTestFactory$TransactionRequestBuilder#invalidTransactionRequestProvider")
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

  @Test
  void testGetTransactions_Success(
      @Mock TransactionCriteria criteria, @Mock List<TransactionDto> response) throws Exception {
    UUID userId = RandomUtils.randomUUID();
    when(transactionService.getTransactions(userId, criteria)).thenReturn(response);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get(basePath + "/transactions/users/" + userId)
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

  @Test
  void testUpdateTransaction_Success(@Mock TransactionDto response) throws Exception {
    UUID transactionId = RandomUtils.randomUUID();
    TransactionUpdateRequestDto request =
        TransactionTestFactory.TransactionUpdateRequestBuilder.defaultInstance();
    when(transactionService.updateTransaction(transactionId, request)).thenReturn(response);

    mockMvc
        .perform(
            MockMvcRequestBuilders.put(basePath + "/transactions/" + transactionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.toString())
                .with(MockAdminRequestPostProcessor.mockAdmin()))
        .andExpect(status().isOk());
  }

  @Test
  void testUpdateTransaction_NotFound() throws Exception {
    UUID transactionId = RandomUtils.randomUUID();
    TransactionUpdateRequestDto request =
        TransactionTestFactory.TransactionUpdateRequestBuilder.defaultInstance();
    when(transactionService.updateTransaction(transactionId, request))
        .thenThrow(ResourceNotFoundException.class);

    mockMvc
        .perform(
            MockMvcRequestBuilders.put(basePath + "/transactions/" + transactionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.toString())
                .with(MockAdminRequestPostProcessor.mockAdmin()))
        .andExpect(status().isNotFound());
  }

  @Test
  void testUpdateTransaction_Forbidden() throws Exception {
    UUID transactionId = RandomUtils.randomUUID();
    TransactionUpdateRequestDto request =
        TransactionTestFactory.TransactionUpdateRequestBuilder.defaultInstance();

    mockMvc
        .perform(
            MockMvcRequestBuilders.put(basePath + "/transactions/" + transactionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.toString())
                .with(SecurityMockMvcRequestPostProcessors.anonymous()))
        .andExpect(status().isForbidden());
  }

  @Test
  void testDeleteTransaction_Success() throws Exception {
    UUID transactionId = RandomUtils.randomUUID();

    Mockito.doNothing().when(transactionService).deleteTransaction(transactionId);

    mockMvc
        .perform(
            MockMvcRequestBuilders.delete(basePath + "/transactions/" + transactionId)
                .with(MockAdminRequestPostProcessor.mockAdmin()))
        .andExpect(status().isNoContent());
  }

  @Test
  void testDeleteTransaction_NotFound() throws Exception {
    UUID transactionId = RandomUtils.randomUUID();
    Mockito.doThrow(ResourceNotFoundException.class)
        .when(transactionService)
        .deleteTransaction(transactionId);

    mockMvc
        .perform(
            MockMvcRequestBuilders.delete(basePath + "/transactions/" + transactionId)
                .with(MockAdminRequestPostProcessor.mockAdmin()))
        .andExpect(status().isNotFound());
  }

  @Test
  void testDeleteTransaction_Forbidden() throws Exception {
    UUID transactionId = RandomUtils.randomUUID();

    mockMvc
        .perform(
            MockMvcRequestBuilders.delete(basePath + "/transactions/" + transactionId)
                .with(SecurityMockMvcRequestPostProcessors.anonymous()))
        .andExpect(status().isForbidden());
  }

  @Test
  void testCreateSubscription_Success(@Mock SubscriptionDto subscriptionDto) throws Exception {
    UUID userId = RandomUtils.randomUUID();
    SubscriptionRequestDto request =
        SubscriptionTestFactory.SubscriptionRequestBuilder.builder().withUserId(userId).build();

    when(subscriptionService.createSubscription(request)).thenReturn(subscriptionDto);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(basePath + "/subscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.toString())
                .with(MockUserRequestPostProcessor.mockUser(userId)))
        .andExpect(status().isCreated());
  }

  @ParameterizedTest
  @MethodSource(
      "it.moneyverse.transaction.model.SubscriptionTestFactory$SubscriptionRequestBuilder#invalidRecurrenceDtoProvider")
  void testCreateSubscription_BadRequest(Supplier<SubscriptionRequestDto> requestSupplier)
      throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(basePath + "/subscriptions")
                .content(requestSupplier.get().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockUserRequestPostProcessor.mockUser(RandomUtils.randomString(15))))
        .andExpect(status().isBadRequest());
  }
}
