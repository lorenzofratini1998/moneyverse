package it.moneyverse.transaction.runtime.controllers;

import it.moneyverse.transaction.model.dto.TransactionCriteria;
import it.moneyverse.transaction.model.dto.TransactionDto;
import it.moneyverse.transaction.model.dto.TransactionRequestDto;
import it.moneyverse.transaction.model.dto.TransactionUpdateRequestDto;
import it.moneyverse.transaction.services.TransactionService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "${spring.security.base-path}")
@Validated
public class TransactionManagementController implements TransactionOperations {

  private final TransactionService transactionService;

  public TransactionManagementController(TransactionService transactionService) {
    this.transactionService = transactionService;
  }

  @Override
  @PostMapping("/transactions")
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("#request.username == authentication.name")
  public TransactionDto createTransaction(@RequestBody TransactionRequestDto request) {
    return transactionService.createTransaction(request);
  }

  @Override
  @GetMapping("/transactions")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "hasRole(T(it.moneyverse.core.enums.UserRoleEnum).ADMIN.name()) or (#criteria.username.isPresent() and #criteria.username.get().equals(authentication.name))")
  public List<TransactionDto> getTransactions(TransactionCriteria criteria) {
    return transactionService.getTransactions(criteria);
  }

  @Override
  @GetMapping("/transactions/{transactionId}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "hasRole(T(it.moneyverse.core.enums.UserRoleEnum).ADMIN.name()) or (@transactionRepository.existsByUsernameAndTransactionId(authentication.name, #transactionId))")
  public TransactionDto getTransaction(@PathVariable UUID transactionId) {
    return transactionService.getTransaction(transactionId);
  }

  @Override
  @PutMapping("/transactions/{transactionId}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "hasRole(T(it.moneyverse.core.enums.UserRoleEnum).ADMIN.name()) or (@transactionRepository.existsByUsernameAndTransactionId(authentication.name, #transactionId))")
  public TransactionDto updateTransaction(
      @PathVariable UUID transactionId, @RequestBody TransactionUpdateRequestDto request) {
    return transactionService.updateTransaction(transactionId, request);
  }
}
