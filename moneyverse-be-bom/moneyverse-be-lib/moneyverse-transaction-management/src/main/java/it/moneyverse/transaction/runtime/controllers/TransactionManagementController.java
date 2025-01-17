package it.moneyverse.transaction.runtime.controllers;

import it.moneyverse.transaction.model.dto.TransactionDto;
import it.moneyverse.transaction.model.dto.TransactionRequestDto;
import it.moneyverse.transaction.services.TransactionService;
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
}
