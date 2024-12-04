package it.moneyverse.account.runtime.controllers;

import it.moneyverse.account.model.dto.AccountCriteria;
import it.moneyverse.account.model.dto.AccountDto;
import it.moneyverse.account.model.dto.AccountRequestDto;
import it.moneyverse.account.services.AccountService;
import it.moneyverse.core.utils.SecurityContextUtils;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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

  @Override
  @GetMapping("/accounts")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("hasRole(T(it.moneyverse.core.enums.UserRoleEnum).ADMIN.name()) or (#criteria.username.isPresent() and #criteria.username.get().equals(authentication.name))")
  public List<AccountDto> getAccounts(AccountCriteria criteria) {
    return accountService.findAccounts(criteria);
  }
}
