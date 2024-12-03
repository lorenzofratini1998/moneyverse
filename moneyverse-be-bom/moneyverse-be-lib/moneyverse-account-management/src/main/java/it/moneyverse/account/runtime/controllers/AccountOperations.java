package it.moneyverse.account.runtime.controllers;

import it.moneyverse.account.model.dto.AccountCriteria;
import it.moneyverse.account.model.dto.AccountDto;
import it.moneyverse.account.model.dto.AccountRequestDto;
import jakarta.validation.Valid;
import java.util.List;

public interface AccountOperations {

  AccountDto createAccount(@Valid AccountRequestDto request);

  List<AccountDto> getAccounts(AccountCriteria criteria);
}
