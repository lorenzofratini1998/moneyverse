package it.moneyverse.account.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.moneyverse.account.model.dto.AccountCriteria;
import it.moneyverse.account.model.dto.AccountDto;
import it.moneyverse.account.model.dto.AccountRequestDto;
import it.moneyverse.account.model.entities.Account;
import it.moneyverse.account.model.repositories.AccountRepository;
import it.moneyverse.core.enums.AccountCategoryEnum;
import it.moneyverse.core.exceptions.ResourceAlreadyExistsException;
import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.model.dto.PageCriteria;
import it.moneyverse.core.model.dto.SortCriteria;
import it.moneyverse.test.utils.RandomUtils;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

/**
 * Unit test for {@link AccountManagementService}
 */
@ExtendWith(MockitoExtension.class)
public class AccountManagementServiceTest {

  @InjectMocks private AccountManagementService accountManagementService;

  @Mock private AccountRepository accountRepository;
  @Mock private UserServiceGrpcClient userServiceClient;

  @Test
  void givenAccountRequest_WhenCreateAccount_ThenReturnCreatedAccount(@Mock Account account) {
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

    when(accountRepository.findDefaultAccountByUser(request.username()))
        .thenReturn(Optional.empty());
    when(accountRepository.save(any(Account.class))).thenReturn(account);
    when(userServiceClient.checkIfUserExists(username)).thenReturn(true);

    AccountDto result = accountManagementService.createAccount(request);

    assertNotNull(result);
    verify(accountRepository, times(1)).findDefaultAccountByUser(request.username());
    verify(accountRepository, times(1)).save(any(Account.class));
    verify(userServiceClient, times(1)).checkIfUserExists(username);
    verify(accountRepository, times(1))
        .existsByUsernameAndAccountName(username, request.accountName());
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
}
