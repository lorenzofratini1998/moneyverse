package it.moneyverse.account.runtime.controllers;

import it.moneyverse.account.model.dto.AccountDto;
import it.moneyverse.account.model.dto.AccountRequestDto;
import it.moneyverse.account.services.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "${spring.security.base-path}")
@Validated
public class AccountManagementController implements AccountOperations{

  private final AccountService accountService;

  public AccountManagementController(AccountService accountService) {
    this.accountService = accountService;
  }

  @Override
  @PostMapping("/accounts")
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasRole(T(it.moneyverse.core.enums.UserRoleEnum).ADMIN.name()) or #request.username == authentication.name")
  public AccountDto createAccount(@RequestBody AccountRequestDto request) {
    return accountService.createAccount(request);
  }
}
