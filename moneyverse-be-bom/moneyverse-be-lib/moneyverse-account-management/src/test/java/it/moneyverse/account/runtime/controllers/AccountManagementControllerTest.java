package it.moneyverse.account.runtime.controllers;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.moneyverse.account.boot.AccountAutoConfiguration;
import it.moneyverse.account.model.AccountTestFactory;
import it.moneyverse.account.model.dto.*;
import it.moneyverse.account.services.AccountManagementService;
import it.moneyverse.core.boot.*;
import it.moneyverse.core.exceptions.ResourceAlreadyExistsException;
import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.model.dto.AccountDto;
import it.moneyverse.core.model.events.SseEmitterRepository;
import it.moneyverse.test.runtime.processor.MockAdminRequestPostProcessor;
import it.moneyverse.test.utils.RandomUtils;
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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(
    controllers = AccountManagementController.class,
    excludeAutoConfiguration = {
      DatasourceAutoConfiguration.class,
      UserServiceGrpcClientAutoConfiguration.class,
      CurrencyServiceGrpcClientAutoConfiguration.class,
      RedisAutoConfiguration.class,
      KafkaAutoConfiguration.class,
      OutboxAutoConfiguration.class,
      SseAutoConfiguration.class,
    },
    excludeFilters =
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = AccountAutoConfiguration.class))
@TestPropertySource(
    properties = {
      "spring.data.jpa.repositories.enabled=false",
      "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration"
    })
@ExtendWith(MockitoExtension.class)
class AccountManagementControllerTest {

  @Value("${spring.security.base-path}")
  protected String basePath;

  @Autowired private MockMvc mockMvc;
  @MockitoBean private AccountManagementService accountService;
  @MockitoBean private SseEmitterRepository sseEmitterRepository;

  @Test
  void testCreateAccount_Success(@Mock AccountDto response) throws Exception {
    AccountRequestDto request = AccountTestFactory.AccountRequestDtoBuilder.defaultInstance();

    when(accountService.createAccount(request)).thenReturn(response);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(basePath + "/accounts")
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockAdminRequestPostProcessor.mockAdmin()))
        .andExpect(status().isCreated());
  }

  @Test
  void testCreateAccount_Forbidden() throws Exception {
    AccountRequestDto request = AccountTestFactory.AccountRequestDtoBuilder.defaultInstance();

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(basePath + "/accounts")
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.anonymous()))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @MethodSource(
      "it.moneyverse.account.model.AccountTestFactory$AccountRequestDtoBuilder#invalidAccountRequestProvider")
  void testBadRequest(Supplier<AccountRequestDto> requestSupplier) throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(basePath + "/accounts")
                .content(requestSupplier.get().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockAdminRequestPostProcessor.mockAdmin()))
        .andExpect(status().isBadRequest());
    verify(accountService, never()).createAccount(requestSupplier.get());
  }

  @Test
  void testAccountCreation_AccountAlreadyExists() throws Exception {
    AccountRequestDto request = AccountTestFactory.AccountRequestDtoBuilder.defaultInstance();
    when(accountService.createAccount(request)).thenThrow(ResourceAlreadyExistsException.class);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(basePath + "/accounts")
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockAdminRequestPostProcessor.mockAdmin()))
        .andExpect(status().isConflict());
  }

  @Test
  void testAccountCreation_AccountNotFound() throws Exception {
    AccountRequestDto request = AccountTestFactory.AccountRequestDtoBuilder.defaultInstance();
    when(accountService.createAccount(request)).thenThrow(ResourceNotFoundException.class);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(basePath + "/accounts")
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockAdminRequestPostProcessor.mockAdmin()))
        .andExpect(status().isNotFound());
  }

  @Test
  void testGetAccounts_Success(@Mock AccountCriteria criteria, @Mock List<AccountDto> response)
      throws Exception {
    UUID userId = RandomUtils.randomUUID();
    when(accountService.findAccounts(userId, criteria)).thenReturn(response);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get(basePath + "/accounts/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockAdminRequestPostProcessor.mockAdmin()))
        .andExpect(status().isOk());
  }

  @Test
  void testGetAccounts_Unauthorized() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(basePath + "/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.anonymous()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void testGetAccount_Success(@Mock AccountDto response) throws Exception {
    UUID accountId = RandomUtils.randomUUID();
    when(accountService.findAccountByAccountId(accountId)).thenReturn(response);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get(basePath + "/accounts/" + accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockAdminRequestPostProcessor.mockAdmin()))
        .andExpect(status().isOk());
  }

  @Test
  void testGetAccount_NotFound() throws Exception {
    UUID accountId = RandomUtils.randomUUID();
    when(accountService.findAccountByAccountId(accountId))
        .thenThrow(ResourceNotFoundException.class);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get(basePath + "/accounts/" + accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockAdminRequestPostProcessor.mockAdmin()))
        .andExpect(status().isNotFound());
  }

  @Test
  void testGetAccount_Unauthorized() throws Exception {
    UUID accountId = RandomUtils.randomUUID();

    mockMvc
        .perform(
            MockMvcRequestBuilders.get(basePath + "/accounts/" + accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.anonymous()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void testUpdateAccount_Success(@Mock AccountDto response) throws Exception {
    UUID accountId = RandomUtils.randomUUID();
    AccountUpdateRequestDto request = createAccountUpdateRequest();

    when(accountService.updateAccount(accountId, request)).thenReturn(response);

    mockMvc
        .perform(
            MockMvcRequestBuilders.put(basePath + "/accounts/" + accountId)
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockAdminRequestPostProcessor.mockAdmin()))
        .andExpect(status().isOk());
  }

  @Test
  void testUpdateAccount_NotFound() throws Exception {
    UUID accountId = RandomUtils.randomUUID();
    AccountUpdateRequestDto request = createAccountUpdateRequest();

    when(accountService.updateAccount(accountId, request))
        .thenThrow(ResourceNotFoundException.class);

    mockMvc
        .perform(
            MockMvcRequestBuilders.put(basePath + "/accounts/" + accountId)
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockAdminRequestPostProcessor.mockAdmin()))
        .andExpect(status().isNotFound());
  }

  @Test
  void testUpdateAccount_Forbidden() throws Exception {
    UUID accountId = RandomUtils.randomUUID();
    AccountUpdateRequestDto request = createAccountUpdateRequest();

    mockMvc
        .perform(
            MockMvcRequestBuilders.put(basePath + "/accounts/" + accountId)
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.anonymous()))
        .andExpect(status().isForbidden());
  }

  private AccountUpdateRequestDto createAccountUpdateRequest() {
    return new AccountUpdateRequestDto(
        RandomUtils.randomString(15),
        RandomUtils.randomBigDecimal(),
        RandomUtils.randomBigDecimal(),
        RandomUtils.randomString(15),
        RandomUtils.randomString(15),
        null,
        null);
  }

  @Test
  void testDeleteAccount_Success() throws Exception {
    UUID accountId = RandomUtils.randomUUID();

    Mockito.doNothing().when(accountService).deleteAccount(accountId);

    mockMvc
        .perform(
            MockMvcRequestBuilders.delete(basePath + "/accounts/" + accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockAdminRequestPostProcessor.mockAdmin()))
        .andExpect(status().isNoContent());
  }

  @Test
  void testDeleteAccount_NotFound() throws Exception {
    UUID accountId = RandomUtils.randomUUID();

    Mockito.doThrow(ResourceNotFoundException.class).when(accountService).deleteAccount(accountId);

    mockMvc
        .perform(
            MockMvcRequestBuilders.delete(basePath + "/accounts/" + accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockAdminRequestPostProcessor.mockAdmin()))
        .andExpect(status().isNotFound());
  }

  @Test
  void testDeleteAccount_Forbidden() throws Exception {
    UUID accountId = RandomUtils.randomUUID();

    mockMvc
        .perform(
            MockMvcRequestBuilders.delete(basePath + "/accounts/" + accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.anonymous()))
        .andExpect(status().isForbidden());
  }

  @Test
  void testGetCategories_Success(@Mock AccountCategoryDto accountCategoryDto) throws Exception {
    when(accountService.getAccountCategories()).thenReturn(List.of(accountCategoryDto));

    mockMvc
        .perform(
            MockMvcRequestBuilders.get(basePath + "/accounts/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.jwt()))
        .andExpect(status().isOk());
  }
}
