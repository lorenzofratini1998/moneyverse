package it.moneyverse.transaction.runtime.controllers;

import it.moneyverse.transaction.model.dto.TransactionDto;
import it.moneyverse.transaction.model.dto.TransactionRequestDto;
import jakarta.validation.Valid;

public interface TransactionOperations {
  TransactionDto createTransaction(@Valid TransactionRequestDto request);
}
