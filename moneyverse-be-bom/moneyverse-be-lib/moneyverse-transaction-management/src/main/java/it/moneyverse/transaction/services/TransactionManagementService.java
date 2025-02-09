package it.moneyverse.transaction.services;

import it.moneyverse.core.enums.SortAttribute;
import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.model.dto.PageCriteria;
import it.moneyverse.core.model.dto.SortCriteria;
import it.moneyverse.transaction.enums.TransactionSortAttributeEnum;
import it.moneyverse.transaction.model.dto.*;
import it.moneyverse.transaction.model.entities.Tag;
import it.moneyverse.transaction.model.entities.Transaction;
import it.moneyverse.transaction.model.repositories.TagRepository;
import it.moneyverse.transaction.model.repositories.TransactionRepository;
import it.moneyverse.transaction.utils.mapper.TransactionMapper;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionManagementService implements TransactionService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TransactionManagementService.class);

  private final TransactionRepository transactionRepository;
  private final TagRepository tagRepository;
  private final TransactionServiceHelper transactionServiceHelper;

  public TransactionManagementService(
      TransactionRepository transactionRepository,
      TagRepository tagRepository,
      TransactionServiceHelper transactionServiceHelper) {
    this.transactionRepository = transactionRepository;
    this.tagRepository = tagRepository;
    this.transactionServiceHelper = transactionServiceHelper;
  }

  @Override
  @Transactional
  public List<TransactionDto> createTransactions(TransactionRequestDto request) {
    List<Transaction> transactions =
        request.transactions().stream()
            .map(transaction -> createTransaction(request.userId(), transaction))
            .toList();

    return TransactionMapper.toTransactionDto(transactionRepository.saveAll(transactions));
  }

  private Transaction createTransaction(UUID userId, TransactionRequestItemDto request) {
    transactionServiceHelper.checkIfAccountExists(request.accountId());
    transactionServiceHelper.checkIfCategoryExists(request.categoryId());
    transactionServiceHelper.checkIfCurrencyExists(request.currency());
    Set<Tag> tags = getTransactionTags(request.tags());
    if (request.tags() != null && !request.tags().isEmpty()) {
      getTransactionTags(request.tags());
    }
    LOGGER.info(
        "Creating transaction for account {} and category {}",
        request.accountId(),
        request.categoryId());
    return tags.isEmpty()
        ? TransactionMapper.toTransaction(userId, request)
        : TransactionMapper.toTransaction(userId, request, tags);
  }

  @Override
  @Transactional(readOnly = true)
  public List<TransactionDto> getTransactions(UUID userId, TransactionCriteria criteria) {
    if (criteria.getPage() == null) {
      criteria.setPage(new PageCriteria());
    }
    if (criteria.getSort() == null) {
      criteria.setSort(
          new SortCriteria<>(
              SortAttribute.getDefault(TransactionSortAttributeEnum.class), Sort.Direction.DESC));
    }
    LOGGER.info("Finding transactions with filters: {}", criteria);
    return TransactionMapper.toTransactionDto(
        transactionRepository.findTransactions(userId, criteria));
  }

  @Override
  @Transactional(readOnly = true)
  public TransactionDto getTransaction(UUID transactionId) {
    return TransactionMapper.toTransactionDto(findTransactionById(transactionId));
  }

  private Transaction findTransactionById(UUID transactionId) {
    return transactionRepository
        .findById(transactionId)
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    "Transaction with id %s not found".formatted(transactionId)));
  }

  @Override
  @Transactional
  public TransactionDto updateTransaction(UUID transactionId, TransactionUpdateRequestDto request) {
    Transaction transaction = findTransactionById(transactionId);
    if (request.currency() != null) {
      transactionServiceHelper.checkIfCurrencyExists(request.currency());
    }
    Set<Tag> tags = getTransactionTags(request.tags());
    transaction = TransactionMapper.partialUpdate(transaction, request, tags);
    TransactionDto result =
        TransactionMapper.toTransactionDto(transactionRepository.save(transaction));
    LOGGER.info(
        "Updated transaction: {} for user {}", result.getTransactionId(), result.getUserId());
    return result;
  }

  private Set<Tag> getTransactionTags(Set<UUID> tagIds) {
    if (tagIds == null || tagIds.isEmpty()) {
      return Collections.emptySet();
    }
    return tagIds.stream()
        .map(
            tagId ->
                tagRepository
                    .findById(tagId)
                    .orElseThrow(
                        () ->
                            new ResourceNotFoundException(
                                "Tag %s does not exist".formatted(tagId))))
        .collect(Collectors.toSet());
  }

  @Override
  @Transactional
  public void deleteTransaction(UUID transactionId) {
    Transaction transaction = findTransactionById(transactionId);
    transactionRepository.delete(transaction);
    LOGGER.info(
        "Deleted transaction: {} for user {}",
        transaction.getTransactionId(),
        transaction.getUserId());
  }

  @Override
  public void deleteAllTransactionsByUserId(UUID userId) {
    transactionServiceHelper.checkIfUserExists(userId);
    LOGGER.info("Deleting transactions by userId {}", userId);
    transactionRepository.deleteAll(transactionRepository.findTransactionByUserId(userId));
  }

  @Override
  public void deleteAllTransactionsByAccountId(UUID accountId) {
    transactionServiceHelper.checkIfAccountExists(accountId);
    LOGGER.info("Deleting transactions by account id {}", accountId);
    transactionRepository.deleteAll(transactionRepository.findTransactionByAccountId(accountId));
  }

  @Override
  public void removeBudgetFromTransactions(UUID categoryId) {
    transactionServiceHelper.checkIfCategoryExists(categoryId);
    LOGGER.info("Removing category {} from transactions", categoryId);
    List<Transaction> transactions = transactionRepository.findTransactionByBudgetId(categoryId);
    transactions.forEach(transaction -> transaction.setBudgetId(null));
    transactionRepository.saveAll(transactions);
  }
}
