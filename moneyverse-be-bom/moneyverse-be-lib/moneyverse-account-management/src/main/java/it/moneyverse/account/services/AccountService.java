package it.moneyverse.account.services;

import it.moneyverse.account.model.dto.*;
import it.moneyverse.core.model.dto.AccountDto;
import java.util.List;
import java.util.UUID;

public interface AccountService {

  AccountDto createAccount(AccountRequestDto accountRequestDto);

  List<AccountDto> findAccounts(UUID userId, AccountCriteria criteria);

  AccountDto findAccountByAccountId(UUID accountId);

  AccountDto updateAccount(UUID accountId, AccountUpdateRequestDto request);

  void deleteAccount(UUID accountId);

  void deleteAccountsByUserId(UUID userId);

  List<AccountCategoryDto> getAccountCategories();
}
