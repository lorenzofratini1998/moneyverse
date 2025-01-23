package it.moneyverse.transaction.utils.mapper;

import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.transaction.model.dto.TransactionDto;
import it.moneyverse.transaction.model.dto.TransactionRequestDto;
import it.moneyverse.transaction.model.dto.TransactionUpdateRequestDto;
import it.moneyverse.transaction.model.entities.Transaction;
import it.moneyverse.transaction.model.repositories.TagRepository;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionMapper {

  public static Transaction toTransaction(
      TransactionRequestDto request, TagRepository tagRepository) {
    if (request == null) {
      return null;
    }
    Transaction transaction = new Transaction();
    transaction.setUsername(request.username());
    transaction.setAccountId(request.accountId());
    transaction.setBudgetId(request.budgetId());
    transaction.setDate(request.date());
    transaction.setDescription(request.description());
    transaction.setAmount(request.amount());
    transaction.setCurrency(request.currency());

    if (request.tags() != null && !request.tags().isEmpty()) {
      request.tags().stream()
          .map(
              tagId ->
                  tagRepository
                      .findById(tagId)
                      .orElseThrow(
                          () ->
                              new ResourceNotFoundException(
                                  "Tag %s does not exist".formatted(tagId))))
          .forEach(transaction::addTag);
    }
    return transaction;
  }

  public static TransactionDto toTransactionDto(Transaction transaction) {
    if (transaction == null) {
      return null;
    }
    return TransactionDto.builder()
        .withTransactionId(transaction.getTransactionId())
        .withUsername(transaction.getUsername())
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
      Transaction transaction, TransactionUpdateRequestDto request, TagRepository tagRepository) {
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
    if (request.tags() != null && !request.tags().isEmpty()) {
      transaction.setTags(
          request.tags().stream()
              .map(
                  tagId ->
                      tagRepository
                          .findById(tagId)
                          .orElseThrow(
                              () ->
                                  new ResourceNotFoundException(
                                      "Tag %s does not exist".formatted(tagId))))
              .collect(Collectors.toSet()));
    }
    return transaction;
  }

  private TransactionMapper() {}
}
