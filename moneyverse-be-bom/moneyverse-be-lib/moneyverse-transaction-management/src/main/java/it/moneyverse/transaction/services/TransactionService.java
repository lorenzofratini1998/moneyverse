package it.moneyverse.transaction.services;

import it.moneyverse.transaction.model.dto.TransactionCriteria;
import it.moneyverse.transaction.model.dto.TransactionDto;
import it.moneyverse.transaction.model.dto.TransactionRequestDto;
import it.moneyverse.transaction.model.dto.TransactionUpdateRequestDto;
import java.util.List;
import java.util.UUID;

public interface TransactionService {

  List<TransactionDto> createTransactions(TransactionRequestDto request);

  List<TransactionDto> getTransactions(UUID userId, TransactionCriteria criteria);

  TransactionDto getTransaction(UUID transactionId);

  TransactionDto updateTransaction(UUID transactionId, TransactionUpdateRequestDto request);

  void deleteTransaction(UUID transactionId);

  void deleteAllTransactionsByUserId(UUID userId);

  void deleteAllTransactionsByAccountId(UUID accountId);
  void removeBudgetFromTransactions(UUID budgetId);
}
