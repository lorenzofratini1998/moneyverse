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
  @PreAuthorize("@securityService.isAuthenticatedUserOwner(#request.userId())")
  public List<TransactionDto> createTransaction(@RequestBody TransactionRequestDto request) {
    return transactionService.createTransactions(request);
  }

  @Override
  @GetMapping("/transactions/users/{userId}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("@securityService.isAuthenticatedUserOwner(#userId)")
  public List<TransactionDto> getTransactions(
      @PathVariable UUID userId, TransactionCriteria criteria) {
    return transactionService.getTransactions(userId, criteria);
  }

  @Override
  @GetMapping("/transactions/{transactionId}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "@transactionRepository.existsByUserIdAndTransactionId(@securityService.getAuthenticatedUserId(), #transactionId)")
  public TransactionDto getTransaction(@PathVariable UUID transactionId) {
    return transactionService.getTransaction(transactionId);
  }

  @Override
  @PutMapping("/transactions/{transactionId}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "@transactionRepository.existsByUserIdAndTransactionId(@securityService.getAuthenticatedUserId(), #transactionId)")
  public TransactionDto updateTransaction(
      @PathVariable UUID transactionId, @RequestBody TransactionUpdateRequestDto request) {
    return transactionService.updateTransaction(transactionId, request);
  }

  @Override
  @DeleteMapping("/transactions/{transactionId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize(
      "@transactionRepository.existsByUserIdAndTransactionId(@securityService.getAuthenticatedUserId(), #transactionId)")
  public void deleteTransaction(@PathVariable UUID transactionId) {
    transactionService.deleteTransaction(transactionId);
  }
}
