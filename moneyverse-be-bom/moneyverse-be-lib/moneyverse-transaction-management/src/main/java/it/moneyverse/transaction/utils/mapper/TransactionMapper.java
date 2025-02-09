package it.moneyverse.transaction.utils.mapper;

import it.moneyverse.transaction.model.dto.TransactionDto;
import it.moneyverse.transaction.model.dto.TransactionRequestItemDto;
import it.moneyverse.transaction.model.dto.TransactionUpdateRequestDto;
import it.moneyverse.transaction.model.entities.Tag;
import it.moneyverse.transaction.model.entities.Transaction;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TransactionMapper {

  public static Transaction toTransaction(
      UUID userId, TransactionRequestItemDto request, Set<Tag> tags) {
    Transaction transaction = toTransaction(userId, request);
    transaction.setTags(tags);
    return transaction;
  }

  public static Transaction toTransaction(UUID userId, TransactionRequestItemDto request) {
    if (request == null) {
      return null;
    }
    Transaction transaction = new Transaction();
    transaction.setUserId(userId);
    transaction.setAccountId(request.accountId());
    transaction.setBudgetId(request.categoryId());
    transaction.setDate(request.date());
    transaction.setDescription(request.description());
    transaction.setAmount(request.amount());
    transaction.setCurrency(request.currency());
    return transaction;
  }

  public static TransactionDto toTransactionDto(Transaction transaction) {
    if (transaction == null) {
      return null;
    }
    return TransactionDto.builder()
        .withTransactionId(transaction.getTransactionId())
        .withUserId(transaction.getUserId())
        .withAccountId(transaction.getAccountId())
        .withBudgetId(transaction.getBudgetId())
        .withDate(transaction.getDate())
        .withDescription(transaction.getDescription())
        .withAmount(transaction.getAmount())
        .withCurrency(transaction.getCurrency())
        .withTags(TagMapper.toTagDto(transaction.getTags()))
        .build();
  }

  public static List<TransactionDto> toTransactionDto(List<Transaction> entities) {
    if (entities.isEmpty()) {
      return Collections.emptyList();
    }
    return entities.stream().map(TransactionMapper::toTransactionDto).toList();
  }

  public static Transaction partialUpdate(
      Transaction transaction, TransactionUpdateRequestDto request, Set<Tag> tags) {
    transaction = partialUpdate(transaction, request);
    if (request.tags() != null && !request.tags().isEmpty()) {
      transaction.setTags(tags);
    }
    return transaction;
  }

  public static Transaction partialUpdate(
      Transaction transaction, TransactionUpdateRequestDto request) {
    if (request == null) {
      return null;
    }
    if (request.accountId() != null) {
      transaction.setAccountId(request.accountId());
    }
    if (request.budgetId() != null) {
      transaction.setBudgetId(request.budgetId());
    }
    if (request.date() != null) {
      transaction.setDate(request.date());
    }
    if (request.description() != null) {
      transaction.setDescription(request.description());
    }
    if (request.currency() != null) {
      transaction.setCurrency(request.currency());
    }
    if (request.amount() != null) {
      transaction.setAmount(request.amount());
    }
    return transaction;
  }

  private TransactionMapper() {}
}
