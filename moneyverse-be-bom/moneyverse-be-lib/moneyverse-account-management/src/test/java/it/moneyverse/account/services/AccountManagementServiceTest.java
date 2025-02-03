package it.moneyverse.account.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import it.moneyverse.account.model.dto.*;
import it.moneyverse.account.model.entities.Account;
import it.moneyverse.account.model.entities.AccountCategory;
import it.moneyverse.account.model.event.AccountDeletionEvent;
import it.moneyverse.account.model.repositories.AccountCategoryRepository;
import it.moneyverse.account.model.repositories.AccountRepository;
import it.moneyverse.account.utils.mapper.AccountMapper;
import it.moneyverse.core.exceptions.ResourceAlreadyExistsException;
import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.model.beans.AccountDeletionTopic;
import it.moneyverse.core.model.dto.PageCriteria;
import it.moneyverse.core.model.dto.SortCriteria;
import it.moneyverse.core.services.CurrencyServiceGrpcClient;
import it.moneyverse.core.services.MessageProducer;
import it.moneyverse.core.services.UserServiceGrpcClient;
import it.moneyverse.test.utils.RandomUtils;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.SendResult;

/** Unit test for {@link AccountManagementService} */
@ExtendWith(MockitoExtension.class)
class AccountManagementServiceTest {

  @InjectMocks private AccountManagementService accountManagementService;

  @Mock private AccountCategoryRepository accountCategoryRepository;
  @Mock private AccountRepository accountRepository;
  @Mock private UserServiceGrpcClient userServiceClient;
  @Mock private CurrencyServiceGrpcClient currencyServiceClient;
  @Mock private MessageProducer<UUID, String> messageProducer;
  @Mock private AccountDeletionTopic accountDeletionTopic;
  private MockedStatic<AccountMapper> mapper;

  @BeforeEach
  public void setup() {
    mapper = mockStatic(AccountMapper.class);
  }

  @AfterEach
  public void tearDown() {
    mapper.close();
  }

  @Test
  void givenAccountRequest_WhenCreateAccount_ThenReturnCreatedAccount(
      @Mock Account account, @Mock AccountCategory category, @Mock AccountDto accountDto) {
    final UUID userId = RandomUtils.randomUUID();
    final String categoryName = RandomUtils.randomString(15).toUpperCase();
    AccountRequestDto request = createAccountRequestDto(userId, categoryName);

    when(userServiceClient.checkIfUserExists(userId)).thenReturn(true);
    when(currencyServiceClient.checkIfCurrencyExists(request.currency())).thenReturn(true);
    when(accountRepository.existsByUserIdAndAccountName(userId, request.accountName()))
        .thenReturn(false);
    when(accountCategoryRepository.findByName(categoryName)).thenReturn(Optional.of(category));
    mapper.when(() -> AccountMapper.toAccount(request, category)).thenReturn(account);
    when(accountRepository.findDefaultAccountsByUserId(request.userId()))
        .thenReturn(Collections.emptyList());
    when(accountRepository.save(any(Account.class))).thenReturn(account);
    mapper.when(() -> AccountMapper.toAccountDto(account)).thenReturn(accountDto);

    accountDto = accountManagementService.createAccount(request);

    assertNotNull(accountDto);
    verify(userServiceClient, times(1)).checkIfUserExists(userId);
    verify(currencyServiceClient, times(1)).checkIfCurrencyExists(request.currency());
    verify(accountRepository, times(1)).existsByUserIdAndAccountName(userId, request.accountName());
    verify(accountCategoryRepository, times(1)).findByName(categoryName);
    mapper.verify(() -> AccountMapper.toAccount(request, category), times(1));
    verify(accountRepository, times(1)).findDefaultAccountsByUserId(request.userId());
    verify(accountRepository, times(1)).save(any(Account.class));
    mapper.verify(() -> AccountMapper.toAccountDto(account), times(1));
  }

  @Test
  void givenAccountRequest_WhenCreateAccount_ThenUserNotFound() {
    final UUID userId = RandomUtils.randomUUID();
    final String categoryName = RandomUtils.randomString(15).toUpperCase();
    AccountRequestDto request = createAccountRequestDto(userId, categoryName);

    when(userServiceClient.checkIfUserExists(userId)).thenReturn(false);

    assertThrows(
        ResourceNotFoundException.class, () -> accountManagementService.createAccount(request));

    verify(accountCategoryRepository, never()).findByName(categoryName);
    verify(accountRepository, never()).findDefaultAccountsByUserId(request.userId());
    verify(accountRepository, never()).save(any(Account.class));
    verify(accountRepository, never()).existsByUserIdAndAccountName(userId, request.accountName());
    verify(userServiceClient, times(1)).checkIfUserExists(userId);
  }

  @Test
  void givenAccountRequest_WhenCreateAccount_ThenCurrencyNotFound() {
    final UUID userId = RandomUtils.randomUUID();
    final String categoryName = RandomUtils.randomString(15).toUpperCase();
    AccountRequestDto request = createAccountRequestDto(userId, categoryName);

    when(userServiceClient.checkIfUserExists(userId)).thenReturn(true);
    when(currencyServiceClient.checkIfCurrencyExists(request.currency())).thenReturn(false);

    assertThrows(
        ResourceNotFoundException.class, () -> accountManagementService.createAccount(request));

    verify(userServiceClient, times(1)).checkIfUserExists(userId);
    verify(currencyServiceClient, times(1)).checkIfCurrencyExists(request.currency());
    verify(accountCategoryRepository, never()).findByName(categoryName);
    verify(accountRepository, never()).findDefaultAccountsByUserId(request.userId());
    verify(accountRepository, never()).save(any(Account.class));
    verify(accountRepository, never()).existsByUserIdAndAccountName(userId, request.accountName());
    verify(userServiceClient, times(1)).checkIfUserExists(userId);
  }

  @Test
  void givenAccountRequest_WhenCreateAccount_ThenAccountAlreadyExists() {
    final UUID userId = RandomUtils.randomUUID();
    final String categoryName = RandomUtils.randomString(15).toUpperCase();
    AccountRequestDto request = createAccountRequestDto(userId, categoryName);

    when(userServiceClient.checkIfUserExists(userId)).thenReturn(true);
    when(currencyServiceClient.checkIfCurrencyExists(request.currency())).thenReturn(true);
    when(accountRepository.existsByUserIdAndAccountName(userId, request.accountName()))
        .thenReturn(true);

    assertThrows(
        ResourceAlreadyExistsException.class,
        () -> accountManagementService.createAccount(request));

    verify(accountCategoryRepository, never()).findByName(categoryName);
    verify(accountRepository, never()).findDefaultAccountsByUserId(request.userId());
    verify(accountRepository, never()).save(any(Account.class));
    verify(userServiceClient, times(1)).checkIfUserExists(userId);
    verify(accountRepository, times(1)).existsByUserIdAndAccountName(userId, request.accountName());
  }

  @Test
  void givenAccountRequest_WhenCreateAccount_ThenCategoryNotFound() {
    final UUID userId = RandomUtils.randomUUID();
    final String categoryName = RandomUtils.randomString(15).toUpperCase();
    AccountRequestDto request = createAccountRequestDto(userId, categoryName);

    when(userServiceClient.checkIfUserExists(userId)).thenReturn(true);
    when(currencyServiceClient.checkIfCurrencyExists(request.currency())).thenReturn(true);
    when(accountRepository.existsByUserIdAndAccountName(userId, request.accountName()))
        .thenReturn(false);
    when(accountCategoryRepository.findByName(categoryName)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class, () -> accountManagementService.createAccount(request));

    verify(accountCategoryRepository, times(1)).findByName(categoryName);
    verify(accountRepository, never()).findDefaultAccountsByUserId(request.userId());
    verify(accountRepository, never()).save(any(Account.class));
    verify(accountRepository, times(1)).existsByUserIdAndAccountName(userId, request.accountName());
    verify(userServiceClient, times(1)).checkIfUserExists(userId);
  }

  private AccountRequestDto createAccountRequestDto(UUID userId, String categoryName) {
    return new AccountRequestDto(
        userId,
        RandomUtils.randomString(15),
        RandomUtils.randomBigDecimal(),
        RandomUtils.randomBigDecimal(),
        categoryName,
        RandomUtils.randomString(15),
        RandomUtils.randomString(3).toUpperCase());
  }

  @Test
  void givenAccountCriteria_WhenGetAccounts_ThenReturnAccountList(
      @Mock AccountCriteria criteria, @Mock List<Account> accounts) {
    when(accountRepository.findAccounts(criteria)).thenReturn(accounts);

    accountManagementService.findAccounts(criteria);

    assertNotNull(accounts);
    verify(criteria, times(1)).setPage(any(PageCriteria.class));
    verify(criteria, times(1)).setSort(any(SortCriteria.class));
    verify(accountRepository, times(1)).findAccounts(criteria);
  }

  @Test
  void givenAccountId_WhenGetAccount_ThenReturnAccountDto(
      @Mock Account account, @Mock AccountDto result) {
    UUID accountId = RandomUtils.randomUUID();
    when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
    when(AccountMapper.toAccountDto(account)).thenReturn(result);

    result = accountManagementService.findAccountByAccountId(accountId);
    assertNotNull(result);
    verify(accountRepository, times(1)).findById(accountId);
    mapper.verify(() -> AccountMapper.toAccountDto(account), times(1));
  }

  @Test
  void givenAccountId_WhenGetAccount_ThenResourceNotFoundException() {
    UUID accountId = RandomUtils.randomUUID();
    when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> accountManagementService.findAccountByAccountId(accountId));
    verify(accountRepository, times(1)).findById(accountId);
    mapper.verify(() -> AccountMapper.toAccountDto(any(Account.class)), never());
  }

  @Test
  void givenAccountId_WhenUpdateAccount_ThenReturnAccountDto(
      @Mock Account account, @Mock AccountCategory category, @Mock AccountDto result) {
    UUID accountId = RandomUtils.randomUUID();
    final String categoryName = RandomUtils.randomString(15);
    AccountUpdateRequestDto request = createAccountUpdateRequestDto(categoryName);

    when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
    when(accountCategoryRepository.findByName(any(String.class))).thenReturn(Optional.of(category));
    when(currencyServiceClient.checkIfCurrencyExists(request.currency())).thenReturn(true);
    mapper.when(() -> AccountMapper.partialUpdate(account, request, category)).thenReturn(account);
    when(accountRepository.save(account)).thenReturn(account);
    mapper.when(() -> AccountMapper.toAccountDto(account)).thenReturn(result);

    result = accountManagementService.updateAccount(accountId, request);

    assertNotNull(result);
    verify(accountRepository, times(1)).findById(accountId);
    verify(accountCategoryRepository, times(1)).findByName(any(String.class));
    verify(currencyServiceClient, times(1)).checkIfCurrencyExists(request.currency());
    mapper.verify(() -> AccountMapper.partialUpdate(account, request, category), times(1));
    verify(accountRepository, times(1)).save(account);
    mapper.verify(() -> AccountMapper.toAccountDto(account), times(1));
  }

  @Test
  void givenAccountId_WhenUpdateAccountAlreadyExistentDefaultAccount_ThenReturnAccountDto(
      @Mock Account account,
      @Mock AccountCategory category,
      @Mock Account defaultAccount,
      @Mock AccountDto result) {
    UUID accountId = RandomUtils.randomUUID();
    AccountUpdateRequestDto request =
        new AccountUpdateRequestDto(
            RandomUtils.randomString(15),
            RandomUtils.randomBigDecimal(),
            RandomUtils.randomBigDecimal(),
            RandomUtils.randomString(15),
            RandomUtils.randomString(15),
            RandomUtils.randomString(3).toUpperCase(),
            Boolean.TRUE);

    when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
    when(accountCategoryRepository.findByName(any(String.class))).thenReturn(Optional.of(category));
    when(currencyServiceClient.checkIfCurrencyExists(request.currency())).thenReturn(true);
    mapper.when(() -> AccountMapper.partialUpdate(account, request, category)).thenReturn(account);
    when(accountRepository.findDefaultAccountsByUserId(any())).thenReturn(List.of(defaultAccount));
    when(defaultAccount.getAccountId()).thenReturn(RandomUtils.randomUUID());
    when(accountRepository.save(defaultAccount)).thenReturn(defaultAccount);
    when(accountRepository.save(account)).thenReturn(account);
    mapper.when(() -> AccountMapper.toAccountDto(account)).thenReturn(result);

    result = accountManagementService.updateAccount(accountId, request);

    assertNotNull(result);
    verify(accountRepository, times(1)).findById(accountId);
    verify(accountCategoryRepository, times(1)).findByName(any(String.class));
    verify(currencyServiceClient, times(1)).checkIfCurrencyExists(request.currency());
    mapper.verify(() -> AccountMapper.partialUpdate(account, request, category), times(1));
    verify(accountRepository, times(1)).findDefaultAccountsByUserId(any());
    verify(accountRepository, times(1)).save(defaultAccount);
    verify(accountRepository, times(1)).save(account);
    mapper.verify(() -> AccountMapper.toAccountDto(account), times(1));
  }

  @Test
  void givenAccountId_WhenUpdateAccount_ThenReturnAccountNotFound() {
    UUID accountId = RandomUtils.randomUUID();
    final String categoryName = RandomUtils.randomString(15);
    AccountUpdateRequestDto request = createAccountUpdateRequestDto(categoryName);

    when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> accountManagementService.updateAccount(accountId, request));

    verify(accountRepository, times(1)).findById(accountId);
    verify(accountCategoryRepository, never()).findByName(any(String.class));
    mapper.verify(
        () ->
            AccountMapper.partialUpdate(
                any(Account.class), any(AccountUpdateRequestDto.class), any(AccountCategory.class)),
        never());
    verify(accountRepository, never()).save(any(Account.class));
    mapper.verify(() -> AccountMapper.toAccountDto(any(Account.class)), never());
  }

  @Test
  void givenAccountId_WhenUpdateAccount_ThenReturnCategoryNotFound(@Mock Account account) {
    UUID accountId = RandomUtils.randomUUID();
    final String categoryName = RandomUtils.randomString(15);
    AccountUpdateRequestDto request = createAccountUpdateRequestDto(categoryName);

    when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
    when(accountCategoryRepository.findByName(categoryName)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> accountManagementService.updateAccount(accountId, request));

    verify(accountRepository, times(1)).findById(accountId);
    verify(accountCategoryRepository, times(1)).findByName(categoryName);
    mapper.verify(
        () ->
            AccountMapper.partialUpdate(
                any(Account.class), any(AccountUpdateRequestDto.class), any(AccountCategory.class)),
        never());
    verify(accountRepository, never()).save(any(Account.class));
    mapper.verify(() -> AccountMapper.toAccountDto(any(Account.class)), never());
  }

  @Test
  void givenAccountId_WhenUpdateAccount_ThenReturnCurrencyNotFound(
      @Mock Account account, @Mock AccountCategory category) {
    UUID accountId = RandomUtils.randomUUID();
    final String categoryName = RandomUtils.randomString(15);
    AccountUpdateRequestDto request = createAccountUpdateRequestDto(categoryName);

    when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
    when(accountCategoryRepository.findByName(categoryName)).thenReturn(Optional.of(category));
    when(currencyServiceClient.checkIfCurrencyExists(request.currency())).thenReturn(false);

    assertThrows(
        ResourceNotFoundException.class,
        () -> accountManagementService.updateAccount(accountId, request));

    verify(accountRepository, times(1)).findById(accountId);
    verify(accountCategoryRepository, times(1)).findByName(categoryName);
    verify(currencyServiceClient, times(1)).checkIfCurrencyExists(request.currency());
    mapper.verify(
        () ->
            AccountMapper.partialUpdate(
                any(Account.class), any(AccountUpdateRequestDto.class), any(AccountCategory.class)),
        never());
    verify(accountRepository, never()).save(any(Account.class));
    mapper.verify(() -> AccountMapper.toAccountDto(any(Account.class)), never());
  }

  private AccountUpdateRequestDto createAccountUpdateRequestDto(String categoryName) {
    return new AccountUpdateRequestDto(
        RandomUtils.randomString(15),
        RandomUtils.randomBigDecimal(),
        RandomUtils.randomBigDecimal(),
        categoryName,
        RandomUtils.randomString(15),
        RandomUtils.randomString(3).toUpperCase(),
        null);
  }

  @Test
  void givenAccountId_WhenDeleteAccount_ThenDeleteAccount(
      @Mock Account account, @Mock CompletableFuture<SendResult<UUID, String>> future) {
    UUID accountId = RandomUtils.randomUUID();

    when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
    when(messageProducer.send(any(AccountDeletionEvent.class), any(String.class)))
        .thenReturn(future);

    accountManagementService.deleteAccount(accountId);

    verify(accountRepository, times(1)).findById(accountId);
    verify(messageProducer, times(1)).send(any(AccountDeletionEvent.class), any(String.class));
  }

  @Test
  void givenAccountId_WhenDeleteAccount_ThenResourceNotFoundException() {
    UUID accountId = RandomUtils.randomUUID();

    when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class, () -> accountManagementService.deleteAccount(accountId));

    verify(accountRepository, times(1)).findById(accountId);
    verify(accountDeletionTopic, never()).name();
    verify(messageProducer, never()).send(any(AccountDeletionEvent.class), any(String.class));
  }

  @Test
  void whenGetAccountCategories_ThenReturnAccountCategories(
      @Mock AccountCategory accountCategory, @Mock AccountCategoryDto accountCategoryDto) {
    when(accountCategoryRepository.findAll()).thenReturn(List.of(accountCategory));
    mapper
        .when(() -> AccountMapper.toAccountCategoryDto(any(AccountCategory.class)))
        .thenReturn(accountCategoryDto);

    accountManagementService.getAccountCategories();

    verify(accountCategoryRepository, times(1)).findAll();
    mapper.verify(() -> AccountMapper.toAccountCategoryDto(any(AccountCategory.class)), times(1));
  }
}
