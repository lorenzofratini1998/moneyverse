package it.moneyverse.account.runtime.controllers;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.moneyverse.account.model.dto.AccountDto;
import it.moneyverse.account.model.dto.AccountRequestDto;
import it.moneyverse.account.services.AccountManagementService;
import it.moneyverse.core.boot.DatasourceAutoConfiguration;
import it.moneyverse.core.boot.SecurityAutoConfiguration;
import it.moneyverse.core.boot.UserServiceGrpcClientAutoConfiguration;
import it.moneyverse.core.enums.AccountCategoryEnum;
import it.moneyverse.core.exceptions.ResourceAlreadyExistsException;
import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.test.utils.RandomUtils;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(
    properties = {"keycloak.host=test", "keycloak.port=0", "keycloak.realm-name=test"},
    controllers = AccountManagementController.class,
    excludeAutoConfiguration = {
      DatasourceAutoConfiguration.class,
      SecurityAutoConfiguration.class,
      UserServiceGrpcClientAutoConfiguration.class
    })
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
public class AccountManagementControllerTest {

  @Value("${spring.security.base-path}")
  protected String basePath;

  @Autowired private MockMvc mockMvc;
  @MockitoBean private AccountManagementService accountService;

  @Test
  void testCreateAccount_Success(@Mock AccountDto response) throws Exception {
    AccountRequestDto request =
        new AccountRequestDto(
            RandomUtils.randomString(15),
            RandomUtils.randomString(15),
            RandomUtils.randomBigDecimal(),
            RandomUtils.randomBigDecimal(),
            RandomUtils.randomEnum(AccountCategoryEnum.class),
            RandomUtils.randomString(15),
            null);
    when(accountService.createAccount(request)).thenReturn(response);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(basePath + "/accounts")
                .header("Authentication", "Bearer token")
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated());
  }

  @ParameterizedTest
  @MethodSource("invalidAccountRequestProvider")
  void testBadRequest(Supplier<AccountRequestDto> requestSupplier) throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(basePath + "/accounts")
                .content(requestSupplier.get().toString())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
    verify(accountService, never()).createAccount(requestSupplier.get());
  }

  @Test
  void testAccountCreation_AccountAlreadyExists() throws Exception {
    AccountRequestDto request =
        new AccountRequestDto(
            RandomUtils.randomString(15),
            RandomUtils.randomString(15),
            RandomUtils.randomBigDecimal(),
            RandomUtils.randomBigDecimal(),
            RandomUtils.randomEnum(AccountCategoryEnum.class),
            RandomUtils.randomString(15),
            null);
    when(accountService.createAccount(request)).thenThrow(ResourceAlreadyExistsException.class);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(basePath + "/accounts")
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isConflict());

  }

  @Test
  void testAccountCreation_AccountNotFound() throws Exception {
    AccountRequestDto request =
        new AccountRequestDto(
            RandomUtils.randomString(15),
            RandomUtils.randomString(15),
            RandomUtils.randomBigDecimal(),
            RandomUtils.randomBigDecimal(),
            RandomUtils.randomEnum(AccountCategoryEnum.class),
            RandomUtils.randomString(15),
            null);
    when(accountService.createAccount(request)).thenThrow(ResourceNotFoundException.class);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(basePath + "/accounts")
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  private static Stream<Supplier<AccountRequestDto>> invalidAccountRequestProvider() {
    return Stream.of(
        AccountManagementControllerTest::createRequestWithNullUsername,
        AccountManagementControllerTest::createRequestWithNullAccountName,
        AccountManagementControllerTest::createRequestWithNullAccountCategory,
        AccountManagementControllerTest::createRequestWithExceedUsername);
  }

  private static AccountRequestDto createRequestWithNullUsername() {
    return new AccountRequestDto(
        null,
        RandomUtils.randomString(15),
        RandomUtils.randomBigDecimal(),
        RandomUtils.randomBigDecimal(),
        RandomUtils.randomEnum(AccountCategoryEnum.class),
        RandomUtils.randomString(15),
        null);
  }

  private static AccountRequestDto createRequestWithNullAccountName() {
    return new AccountRequestDto(
        RandomUtils.randomString(15),
        null,
        RandomUtils.randomBigDecimal(),
        RandomUtils.randomBigDecimal(),
        RandomUtils.randomEnum(AccountCategoryEnum.class),
        RandomUtils.randomString(15),
        null);
  }

  private static AccountRequestDto createRequestWithNullAccountCategory() {
    return new AccountRequestDto(
        RandomUtils.randomString(15),
        RandomUtils.randomString(15),
        RandomUtils.randomBigDecimal(),
        RandomUtils.randomBigDecimal(),
        null,
        RandomUtils.randomString(15),
        null);
  }

  private static AccountRequestDto createRequestWithExceedUsername() {
    final String username = RandomUtils.randomString(100);
    return new AccountRequestDto(
        username,
        RandomUtils.randomString(15),
        RandomUtils.randomBigDecimal(),
        RandomUtils.randomBigDecimal(),
        RandomUtils.randomEnum(AccountCategoryEnum.class),
        RandomUtils.randomString(15),
        null);
  }
}
