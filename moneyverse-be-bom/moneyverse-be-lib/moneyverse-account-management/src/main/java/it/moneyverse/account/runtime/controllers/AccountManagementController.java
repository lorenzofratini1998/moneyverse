package it.moneyverse.account.runtime.controllers;

import it.moneyverse.account.model.dto.*;
import it.moneyverse.account.services.AccountService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "${spring.security.base-path}")
@Validated
public class AccountManagementController implements AccountOperations {

  private final AccountService accountService;

  public AccountManagementController(AccountService accountService) {
    this.accountService = accountService;
  }

  @Override
  @PostMapping("/accounts")
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize(
      "hasRole(T(it.moneyverse.core.enums.UserRoleEnum).ADMIN.name()) or #request.username == authentication.name")
  public AccountDto createAccount(@RequestBody AccountRequestDto request) {
    return accountService.createAccount(request);
  }

  @Override
  @GetMapping("/accounts")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "hasRole(T(it.moneyverse.core.enums.UserRoleEnum).ADMIN.name()) or (#criteria.username.isPresent() and #criteria.username.get().equals(authentication.name))")
  public List<AccountDto> getAccounts(AccountCriteria criteria) {
    return accountService.findAccounts(criteria);
  }

  @Override
  @GetMapping("/accounts/{accountId}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "hasRole(T(it.moneyverse.core.enums.UserRoleEnum).ADMIN.name()) or (@accountRepository.existsByUsernameAndAccountId(authentication.name, #accountId))")
  public AccountDto findAccountById(@PathVariable UUID accountId) {
    return accountService.findAccountByAccountId(accountId);
  }

  @Override
  @PutMapping("/accounts/{accountId}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "hasRole(T(it.moneyverse.core.enums.UserRoleEnum).ADMIN.name()) or (@accountRepository.existsByUsernameAndAccountId(authentication.name, #accountId))")
  public AccountDto updateAccount(@PathVariable UUID accountId, @RequestBody AccountUpdateRequestDto request) {
    return accountService.updateAccount(accountId, request);
  }

  @Override
  @DeleteMapping("/accounts/{accountId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize(
      "hasRole(T(it.moneyverse.core.enums.UserRoleEnum).ADMIN.name()) or (@accountRepository.existsByUsernameAndAccountId(authentication.name, #accountId))")
  public void deleteAccount(@PathVariable UUID accountId) {
    accountService.deleteAccount(accountId);
  }

  @Override
  @GetMapping("/accounts/categories")
  @ResponseStatus(HttpStatus.OK)
  public List<AccountCategoryDto> getAccountCategories() {
    return accountService.getAccountCategories();
  }
}
