package it.moneyverse.account.runtime.controllers;

import it.moneyverse.account.model.dto.AccountDto;
import it.moneyverse.account.model.dto.AccountRequestDto;
import jakarta.validation.Valid;

public interface AccountOperations {

  AccountDto createAccount(@Valid AccountRequestDto request);
}
