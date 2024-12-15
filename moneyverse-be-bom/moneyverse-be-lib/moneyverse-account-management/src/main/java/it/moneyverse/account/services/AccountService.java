package it.moneyverse.account.services;

import it.moneyverse.account.model.dto.AccountCriteria;
import it.moneyverse.account.model.dto.AccountDto;
import it.moneyverse.account.model.dto.AccountRequestDto;
import it.moneyverse.account.model.dto.AccountUpdateRequestDto;

import java.util.List;
import java.util.UUID;

public interface AccountService {

  AccountDto createAccount(AccountRequestDto accountRequestDto);

  List<AccountDto> findAccounts(AccountCriteria criteria);

  AccountDto findAccountByAccountId(UUID accountId);

  AccountDto updateAccount(UUID accountId, AccountUpdateRequestDto request);

  void deleteAccount(UUID accountId);

  void deleteAccountsByUsername(String username);
}
