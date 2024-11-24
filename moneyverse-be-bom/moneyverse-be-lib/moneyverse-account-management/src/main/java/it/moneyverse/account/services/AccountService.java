package it.moneyverse.account.services;

import it.moneyverse.account.model.dto.AccountDto;
import it.moneyverse.account.model.dto.AccountRequestDto;

public interface AccountService {

  AccountDto createAccount(AccountRequestDto accountRequestDto);

}
