package it.moneyverse.transaction.services;

import it.moneyverse.transaction.model.dto.TransactionCriteria;
import it.moneyverse.transaction.model.dto.TransactionDto;
import it.moneyverse.transaction.model.dto.TransactionRequestDto;
import it.moneyverse.transaction.model.dto.TransactionUpdateRequestDto;

import java.util.List;
import java.util.UUID;

public interface TransactionService {

  TransactionDto createTransaction(TransactionRequestDto request);

  List<TransactionDto> getTransactions(TransactionCriteria criteria);

  TransactionDto getTransaction(UUID transactionId);

  TransactionDto updateTransaction(UUID transactionId, TransactionUpdateRequestDto request);

  void deleteTransaction(UUID transactionId);
  void deleteAllTransactionsByUsername(String username);
  void deleteAllTransactionsByAccountId(UUID accountId);
}
