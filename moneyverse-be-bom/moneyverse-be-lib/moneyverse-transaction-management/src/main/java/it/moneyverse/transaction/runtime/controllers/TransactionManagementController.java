package it.moneyverse.transaction.runtime.controllers;

import it.moneyverse.transaction.model.dto.*;
import it.moneyverse.transaction.services.TransactionService;
import it.moneyverse.transaction.services.TransferService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "${spring.security.base-path}")
@Validated
public class TransactionManagementController implements TransactionOperations, TransferOperations {

  private final TransactionService transactionService;
  private final TransferService transferService;

  public TransactionManagementController(
      TransactionService transactionService, TransferService transferService) {
    this.transactionService = transactionService;
    this.transferService = transferService;
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

  @Override
  @PostMapping("/transfer")
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("@securityService.isAuthenticatedUserOwner(#request.userId())")
  public TransferDto createTransfer(@RequestBody TransferRequestDto request) {
    return transferService.createTransfer(request);
  }

  @Override
  @PutMapping("/transfer/{transferId}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "@transferRepository.existsByTransactionFrom_UserIdAndTransactionTo_UserIdAndTransferId(@securityService.getAuthenticatedUserId(), @securityService.getAuthenticatedUserId(), #transferId)")
  public TransferDto updateTransfer(
      @PathVariable UUID transferId, @RequestBody TransferUpdateRequestDto request) {
    return transferService.updateTransfer(transferId, request);
  }

  @Override
  @DeleteMapping("/transfer/{transferId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize(
      "@transferRepository.existsByTransactionFrom_UserIdAndTransactionTo_UserIdAndTransferId(@securityService.getAuthenticatedUserId(), @securityService.getAuthenticatedUserId(), #transferId)")
  public void deleteTransfer(@PathVariable UUID transferId) {
    transferService.deleteTransfer(transferId);
  }

  @Override
  @GetMapping("/transfer/{transferId}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "@transferRepository.existsByTransactionFrom_UserIdAndTransactionTo_UserIdAndTransferId(@securityService.getAuthenticatedUserId(), @securityService.getAuthenticatedUserId(), #transferId)")
  public TransferDto getTransactionsByTransferId(@PathVariable UUID transferId) {
    return transferService.getTransactionsByTransferId(transferId);
  }
}
