package it.moneyverse.transaction.runtime.controllers;

import it.moneyverse.transaction.model.dto.TransactionCriteria;
import it.moneyverse.transaction.model.dto.TransactionDto;
import it.moneyverse.transaction.model.dto.TransactionRequestDto;
import it.moneyverse.transaction.model.dto.TransactionUpdateRequestDto;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

public interface TransactionOperations {

  TransactionDto createTransaction(@Valid TransactionRequestDto request);

  List<TransactionDto> getTransactions(UUID userId, TransactionCriteria criteria);

  TransactionDto getTransaction(UUID transactionId);

  TransactionDto updateTransaction(UUID transactionId, @Valid TransactionUpdateRequestDto request);

  void deleteTransaction(UUID transactionId);
}
