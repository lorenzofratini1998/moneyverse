package it.moneyverse.transaction.services;

import it.moneyverse.core.model.dto.PagedResponseDto;
import it.moneyverse.transaction.model.dto.*;
import java.util.List;
import java.util.UUID;

public interface TransactionService {

  List<TransactionDto> createTransactions(TransactionRequestDto request);

  PagedResponseDto<TransactionDto> getTransactions(UUID userId, TransactionCriteria criteria);

  TransactionDto getTransaction(UUID transactionId);

  TransactionDto updateTransaction(UUID transactionId, TransactionUpdateRequestDto request);

  void deleteTransaction(UUID transactionId);

  void deleteAllTransactionsByUserId(UUID userId);

  void deleteAllTransactionsByAccountId(UUID accountId);

  void removeCategoryFromTransactions(UUID categoryId);

  void removeBudgetFromTransactions(UUID budgetId);
}
