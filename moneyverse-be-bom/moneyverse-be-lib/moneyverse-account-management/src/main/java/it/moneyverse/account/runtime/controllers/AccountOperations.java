package it.moneyverse.account.runtime.controllers;

import it.moneyverse.account.model.dto.*;
import it.moneyverse.core.model.dto.AccountDto;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface AccountOperations {

  AccountDto createAccount(@Valid AccountRequestDto request);

  List<AccountDto> getAccounts(UUID userId, AccountCriteria criteria);

  AccountDto findAccountById(UUID accountId);

  AccountDto updateAccount(UUID accountId, AccountUpdateRequestDto request);

  void deleteAccount(UUID accountId);

  List<AccountCategoryDto> getAccountCategories();

  SseEmitter subscribe(UUID userId);
}
