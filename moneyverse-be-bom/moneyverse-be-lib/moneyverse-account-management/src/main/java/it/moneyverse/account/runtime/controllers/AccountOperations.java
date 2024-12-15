package it.moneyverse.account.runtime.controllers;

import it.moneyverse.account.model.dto.AccountCriteria;
import it.moneyverse.account.model.dto.AccountDto;
import it.moneyverse.account.model.dto.AccountRequestDto;
import it.moneyverse.account.model.dto.AccountUpdateRequestDto;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

public interface AccountOperations {

  AccountDto createAccount(@Valid AccountRequestDto request);

  List<AccountDto> getAccounts(AccountCriteria criteria);

  AccountDto findAccountById(UUID accountId);

  AccountDto updateAccount(UUID accountId, AccountUpdateRequestDto request);

  void deleteAccount(UUID accountId);
}
