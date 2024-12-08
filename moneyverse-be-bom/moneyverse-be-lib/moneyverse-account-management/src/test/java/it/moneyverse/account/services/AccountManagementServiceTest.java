package it.moneyverse.account.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import it.moneyverse.account.model.event.AccountDeletionEvent;
import it.moneyverse.account.model.dto.AccountCriteria;
import it.moneyverse.account.model.dto.AccountDto;
import it.moneyverse.account.model.dto.AccountRequestDto;
import it.moneyverse.account.model.dto.AccountUpdateRequestDto;
import it.moneyverse.account.model.entities.Account;
import it.moneyverse.account.model.repositories.AccountRepository;
import it.moneyverse.account.utils.mapper.AccountMapper;
import it.moneyverse.core.enums.AccountCategoryEnum;
import it.moneyverse.core.exceptions.ResourceAlreadyExistsException;
import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.model.beans.AccountDeletionTopic;
import it.moneyverse.core.model.dto.PageCriteria;
import it.moneyverse.core.model.dto.SortCriteria;
import it.moneyverse.test.utils.RandomUtils;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

  @Mock private AccountRepository accountRepository;
  @Mock private UserServiceGrpcClient userServiceClient;
  @Mock private AccountProducer accountProducer;
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
      @Mock Account account, @Mock AccountDto accountDto) {
    final String username = RandomUtils.randomString(15);
    AccountRequestDto request =
        new AccountRequestDto(
            username,
            RandomUtils.randomString(15),
            RandomUtils.randomBigDecimal(),
            RandomUtils.randomBigDecimal(),
            RandomUtils.randomEnum(AccountCategoryEnum.class),
            RandomUtils.randomString(15),
            null);

    when(userServiceClient.checkIfUserExists(username)).thenReturn(true);
    when(accountRepository.existsByUsernameAndAccountName(username, request.accountName()))
        .thenReturn(false);
    mapper.when(() -> AccountMapper.toAccount(request)).thenReturn(account);
    when(accountRepository.findDefaultAccountByUser(request.username()))
        .thenReturn(Optional.empty());
    when(accountRepository.save(any(Account.class))).thenReturn(account);
    mapper.when(() -> AccountMapper.toAccountDto(account)).thenReturn(accountDto);

    accountDto = accountManagementService.createAccount(request);

    assertNotNull(accountDto);
    verify(userServiceClient, times(1)).checkIfUserExists(username);
    verify(accountRepository, times(1))
        .existsByUsernameAndAccountName(username, request.accountName());
    mapper.verify(() -> AccountMapper.toAccount(request), times(1));
    verify(accountRepository, times(1)).findDefaultAccountByUser(request.username());
    verify(accountRepository, times(1)).save(any(Account.class));
    mapper.verify(() -> AccountMapper.toAccountDto(account), times(1));
  }

  @Test
  void givenAccountRequest_WhenCreateAccount_ThenUserNotFound() {
    final String username = RandomUtils.randomString(15);
    AccountRequestDto request =
        new AccountRequestDto(
            username,
            RandomUtils.randomString(15),
            RandomUtils.randomBigDecimal(),
            RandomUtils.randomBigDecimal(),
            RandomUtils.randomEnum(AccountCategoryEnum.class),
            RandomUtils.randomString(15),
            null);

    when(userServiceClient.checkIfUserExists(username)).thenReturn(false);

    assertThrows(
        ResourceNotFoundException.class, () -> accountManagementService.createAccount(request));

    verify(accountRepository, never()).findDefaultAccountByUser(request.username());
    verify(accountRepository, never()).save(any(Account.class));
    verify(accountRepository, never())
        .existsByUsernameAndAccountName(username, request.accountName());
    verify(userServiceClient, times(1)).checkIfUserExists(username);
  }

  @Test
  void givenAccountRequest_WhenCreateAccount_ThenAccountAlreadyExists() {
    final String username = RandomUtils.randomString(15);
    AccountRequestDto request =
        new AccountRequestDto(
            username,
            RandomUtils.randomString(15),
            RandomUtils.randomBigDecimal(),
            RandomUtils.randomBigDecimal(),
            RandomUtils.randomEnum(AccountCategoryEnum.class),
            RandomUtils.randomString(15),
            null);

    when(userServiceClient.checkIfUserExists(username)).thenReturn(true);
    when(accountRepository.existsByUsernameAndAccountName(username, request.accountName()))
        .thenReturn(true);

    assertThrows(
        ResourceAlreadyExistsException.class,
        () -> accountManagementService.createAccount(request));

    verify(accountRepository, never()).findDefaultAccountByUser(request.username());
    verify(accountRepository, never()).save(any(Account.class));
    verify(userServiceClient, times(1)).checkIfUserExists(username);
    verify(accountRepository, times(1))
        .existsByUsernameAndAccountName(username, request.accountName());
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
      @Mock Account account, @Mock AccountDto result) {
    UUID accountId = RandomUtils.randomUUID();
    AccountUpdateRequestDto request =
        new AccountUpdateRequestDto(
            RandomUtils.randomString(15),
            RandomUtils.randomBigDecimal(),
            RandomUtils.randomBigDecimal(),
            RandomUtils.randomEnum(AccountCategoryEnum.class),
            RandomUtils.randomString(15),
            null);

    when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
    mapper.when(() -> AccountMapper.partialUpdate(account, request)).thenReturn(account);
    when(accountRepository.save(account)).thenReturn(account);
    mapper.when(() -> AccountMapper.toAccountDto(account)).thenReturn(result);

    result = accountManagementService.updateAccount(accountId, request);

    assertNotNull(result);
    verify(accountRepository, times(1)).findById(accountId);
    mapper.verify(() -> AccountMapper.partialUpdate(account, request), times(1));
    verify(accountRepository, times(1)).save(account);
    mapper.verify(() -> AccountMapper.toAccountDto(account), times(1));
  }

  @Test
  void givenAccountId_WhenUpdateAccount_ThenReturnResourceNotFound() {
    UUID accountId = RandomUtils.randomUUID();
    AccountUpdateRequestDto request =
        new AccountUpdateRequestDto(
            RandomUtils.randomString(15),
            RandomUtils.randomBigDecimal(),
            RandomUtils.randomBigDecimal(),
            RandomUtils.randomEnum(AccountCategoryEnum.class),
            RandomUtils.randomString(15),
            null);

    when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> accountManagementService.updateAccount(accountId, request));

    verify(accountRepository, times(1)).findById(accountId);
    mapper.verify(
        () -> AccountMapper.partialUpdate(any(Account.class), any(AccountUpdateRequestDto.class)),
        never());
    verify(accountRepository, never()).save(any(Account.class));
    mapper.verify(() -> AccountMapper.toAccountDto(any(Account.class)), never());
  }

  @Test
  void givenAccountId_WhenDeleteAccount_ThenDeleteAccount(
      @Mock Account account, @Mock CompletableFuture<SendResult<UUID, String>> future) {
    UUID accountId = RandomUtils.randomUUID();
    String accountDeletionTopicName = RandomUtils.randomString(15);

    when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
    when(accountDeletionTopic.name()).thenReturn(accountDeletionTopicName);
    when(accountProducer.send(any(AccountDeletionEvent.class), any(String.class)))
        .thenReturn(future);

    accountManagementService.deleteAccount(accountId);

    verify(accountRepository, times(1)).findById(accountId);
    verify(accountDeletionTopic, times(1)).name();
    verify(accountProducer, times(1)).send(any(AccountDeletionEvent.class), any(String.class));
  }

  @Test
  void givenAccountId_WhenDeleteAccount_ThenResourceNotFoundException() {
    UUID accountId = RandomUtils.randomUUID();

    when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class, () -> accountManagementService.deleteAccount(accountId));

    verify(accountRepository, times(1)).findById(accountId);
    verify(accountDeletionTopic, never()).name();
    verify(accountProducer, never()).send(any(AccountDeletionEvent.class), any(String.class));
  }
}
