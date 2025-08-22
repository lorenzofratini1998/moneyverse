package it.moneyverse.account.runtime.controllers;

import it.moneyverse.account.model.dto.*;
import it.moneyverse.account.services.AccountService;
import it.moneyverse.core.model.dto.AccountDto;
import it.moneyverse.core.model.events.SseEmitterRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping(value = "${spring.security.base-path}")
@Validated
public class AccountManagementController implements AccountOperations {

  private final AccountService accountService;
  private final SseEmitterRepository sseEmitterRepository;

  public AccountManagementController(
      AccountService accountService, SseEmitterRepository sseEmitterRepository) {
    this.accountService = accountService;
    this.sseEmitterRepository = sseEmitterRepository;
  }

  @Override
  @PostMapping("/accounts")
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("@securityService.isAuthenticatedUserOwner(#request.userId())")
  public AccountDto createAccount(@RequestBody AccountRequestDto request) {
    return accountService.createAccount(request);
  }

  @Override
  @GetMapping("/accounts/users/{userId}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("(@securityService.isAuthenticatedUserOwner(#userId))")
  public List<AccountDto> getAccounts(@PathVariable UUID userId, AccountCriteria criteria) {
    return accountService.findAccounts(userId, criteria);
  }

  @Override
  @GetMapping("/accounts/{accountId}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "@accountRepository.existsByUserIdAndAccountId(@securityService.getAuthenticatedUserId(), #accountId)")
  public AccountDto findAccountById(@PathVariable UUID accountId) {
    return accountService.findAccountByAccountId(accountId);
  }

  @Override
  @PutMapping("/accounts/{accountId}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "@accountRepository.existsByUserIdAndAccountId(@securityService.getAuthenticatedUserId(), #accountId)")
  public AccountDto updateAccount(
      @PathVariable UUID accountId, @RequestBody AccountUpdateRequestDto request) {
    return accountService.updateAccount(accountId, request);
  }

  @Override
  @DeleteMapping("/accounts/{accountId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize(
      "@accountRepository.existsByUserIdAndAccountId(@securityService.getAuthenticatedUserId(), #accountId)")
  public void deleteAccount(@PathVariable UUID accountId) {
    accountService.deleteAccount(accountId);
  }

  @Override
  @GetMapping("/accounts/categories")
  @ResponseStatus(HttpStatus.OK)
  public List<AccountCategoryDto> getAccountCategories() {
    return accountService.getAccountCategories();
  }

  @Override
  @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public SseEmitter subscribe(@RequestParam UUID userId) {
    SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
    sseEmitterRepository.add(userId, emitter);

    emitter.onCompletion(() -> sseEmitterRepository.remove(userId, emitter));
    emitter.onTimeout(() -> sseEmitterRepository.remove(userId, emitter));
    emitter.onError((error) -> sseEmitterRepository.remove(userId, emitter));

    return emitter;
  }
}
