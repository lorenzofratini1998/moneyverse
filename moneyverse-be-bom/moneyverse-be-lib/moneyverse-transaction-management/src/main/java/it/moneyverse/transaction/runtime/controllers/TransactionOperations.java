package it.moneyverse.transaction.runtime.controllers;

import it.moneyverse.transaction.model.dto.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

public interface TransactionOperations {

  List<TransactionDto> createTransaction(@Valid TransactionRequestDto request);

  List<TransactionDto> getTransactions(UUID userId, TransactionCriteria criteria);

  TransactionDto getTransaction(UUID transactionId);

  TransactionDto updateTransaction(UUID transactionId, @Valid TransactionUpdateRequestDto request);

  void deleteTransaction(UUID transactionId);
}
