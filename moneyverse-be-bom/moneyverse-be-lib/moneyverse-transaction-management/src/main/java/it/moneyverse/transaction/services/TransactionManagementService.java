package it.moneyverse.transaction.services;

import it.moneyverse.core.enums.SortAttribute;
import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.model.dto.PageCriteria;
import it.moneyverse.core.model.dto.SortCriteria;
import it.moneyverse.core.services.CurrencyServiceClient;
import it.moneyverse.core.services.UserServiceClient;
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
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionManagementService implements TransactionService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TransactionManagementService.class);

  private final AccountServiceClient accountServiceClient;
  private final BudgetServiceClient budgetServiceClient;
  private final UserServiceClient userServiceClient;
  private final CurrencyServiceClient currencyServiceClient;
  private final TransactionRepository transactionRepository;
  private final TagRepository tagRepository;

  public TransactionManagementService(
      AccountServiceClient accountServiceClient,
      BudgetServiceClient budgetServiceClient,
      UserServiceClient userServiceClient,
      CurrencyServiceClient currencyServiceClient,
      TransactionRepository transactionRepository,
      TagRepository tagRepository) {
    this.accountServiceClient = accountServiceClient;
    this.budgetServiceClient = budgetServiceClient;
    this.userServiceClient = userServiceClient;
    this.currencyServiceClient = currencyServiceClient;
    this.transactionRepository = transactionRepository;
    this.tagRepository = tagRepository;
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
    checkIfResourceExists(
        request.accountId(), accountServiceClient::checkIfAccountExists, "Account");
    checkIfResourceExists(
        request.categoryId(), budgetServiceClient::checkIfCategoryExists, "Category");
    checkIfCurrencyExists(request.currency());
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

  private void checkIfResourceExists(
      UUID resourceId, Function<UUID, Boolean> checker, String resourceName) {
    if (Boolean.FALSE.equals(checker.apply(resourceId))) {
      throw new ResourceNotFoundException(
          "The requested %s with ID %s does not exist. Please check your input and try again."
              .formatted(resourceName, resourceId));
    }
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
      checkIfCurrencyExists(request.currency());
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

  private void checkIfCurrencyExists(String currency) {
    if (Boolean.FALSE.equals(currencyServiceClient.checkIfCurrencyExists(currency))) {
      throw new ResourceNotFoundException("Currency %s does not exists".formatted(currency));
    }
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
    if (Boolean.FALSE.equals(userServiceClient.checkIfUserExists(userId))) {
      throw new ResourceNotFoundException("User %s does not exists".formatted(userId));
    }
    LOGGER.info("Deleting transactions by userId {}", userId);
    transactionRepository.deleteAll(transactionRepository.findTransactionByUserId(userId));
  }

  @Override
  public void deleteAllTransactionsByAccountId(UUID accountId) {
    checkIfResourceExists(accountId, accountServiceClient::checkIfAccountExists, "Account");
    LOGGER.info("Deleting transactions by account id {}", accountId);
    transactionRepository.deleteAll(transactionRepository.findTransactionByAccountId(accountId));
  }

  @Override
  public void removeBudgetFromTransactions(UUID budgetId) {
    checkIfResourceExists(budgetId, budgetServiceClient::checkIfCategoryExists, "Budget");
    LOGGER.info("Removing budget {} from transactions", budgetId);
    List<Transaction> transactions = transactionRepository.findTransactionByBudgetId(budgetId);
    transactions.forEach(transaction -> transaction.setBudgetId(null));
    transactionRepository.saveAll(transactions);
  }
}
