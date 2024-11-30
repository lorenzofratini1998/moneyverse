package it.moneyverse.account.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.moneyverse.account.model.dto.AccountDto;
import it.moneyverse.account.model.dto.AccountRequestDto;
import it.moneyverse.account.model.entities.Account;
import it.moneyverse.account.model.repositories.AccountRepository;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AccountManagementServiceTest {

  @InjectMocks
  private AccountManagementService accountManagementService;

  @Mock
  private AccountRepository accountRepository;


  @Test
  void givenAccountRequest_WhenCreateAccount_ThenReturnCreatedAccount(@Mock Account account) {
    final String username = UUID.randomUUID().toString();
    AccountRequestDto request = new AccountRequestDto(username, null, null, null, null, null, null);
    account.setBalance(BigDecimal.valueOf(0));

    when(accountRepository.save(any(Account.class))).thenReturn(account);

    AccountDto result = accountManagementService.createAccount(request);
    assertNotNull(result);
    verify(accountRepository, times(1)).save(any(Account.class));
  }

  @Test
  void givenDefaultAccountExists_WhenCreateAccount_ThenReturnNewNonDefaultAccount(@Mock Account account) {
    final String username = UUID.randomUUID().toString();
    AccountRequestDto request = new AccountRequestDto(username, null, BigDecimal.valueOf(500.0), null,
        null, null, null);

    Account existingDefaultAccount = new Account();
    existingDefaultAccount.setDefault(true);

    account.setBalance(BigDecimal.valueOf(500.0)); //new account

    when(accountRepository.findDefaultAccountByUser(request.username())).thenReturn(
        Optional.of(existingDefaultAccount));
    when(accountRepository.save(any(Account.class))).thenReturn(account);

    AccountDto result = accountManagementService.createAccount(request);

    assertNotNull(result);
    assertFalse(account.isDefault());
    verify(accountRepository, times(1)).save(any(Account.class));
  }
}
