package it.moneyverse.account.services;

import it.moneyverse.account.model.dto.AccountCriteria;
import it.moneyverse.account.model.dto.AccountDto;
import it.moneyverse.account.model.dto.AccountRequestDto;
import java.util.List;

public interface AccountService {

  AccountDto createAccount(AccountRequestDto accountRequestDto);

  List<AccountDto> findAccounts(AccountCriteria criteria);
}
