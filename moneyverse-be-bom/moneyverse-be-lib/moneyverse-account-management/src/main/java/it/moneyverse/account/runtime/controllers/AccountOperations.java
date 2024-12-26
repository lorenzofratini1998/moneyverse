package it.moneyverse.account.runtime.controllers;

import it.moneyverse.account.model.dto.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

public interface AccountOperations {

  AccountDto createAccount(@Valid AccountRequestDto request);

  List<AccountDto> getAccounts(AccountCriteria criteria);

  AccountDto findAccountById(UUID accountId);

  AccountDto updateAccount(UUID accountId, AccountUpdateRequestDto request);

  void deleteAccount(UUID accountId);

  List<AccountCategoryDto> getAccountCategories();
}
