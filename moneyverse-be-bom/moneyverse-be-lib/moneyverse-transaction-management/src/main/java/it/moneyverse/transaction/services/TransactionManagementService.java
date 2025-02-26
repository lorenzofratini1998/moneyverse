package it.moneyverse.transaction.services;

import it.moneyverse.core.enums.EventTypeEnum;
import it.moneyverse.core.enums.SortAttribute;
import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.model.dto.BudgetDto;
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
import it.moneyverse.transaction.runtime.messages.TransactionEventPublisher;
import it.moneyverse.transaction.utils.mapper.TransactionMapper;
import java.util.*;
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
  private final CurrencyServiceClient currencyServiceClient;
  private final AccountServiceClient accountServiceClient;
  private final UserServiceClient userServiceClient;
  private final BudgetServiceClient budgetServiceClient;
  private final TransactionEventPublisher transactionEventPublisher;
  private final TransferService transferService;
  private final SubscriptionService subscriptionService;
  private final TagService tagService;

  public TransactionManagementService(
      TransactionRepository transactionRepository,
      TagRepository tagRepository,
      CurrencyServiceClient currencyServiceClient,
      AccountServiceClient accountServiceClient,
      UserServiceClient userServiceGrpcClient,
      BudgetServiceClient budgetServiceClient,
      TransactionEventPublisher transactionEventPublisher,
      TransferService transferService,
      SubscriptionService subscriptionService,
      TagService tagService) {
    this.transactionRepository = transactionRepository;
    this.tagRepository = tagRepository;
    this.currencyServiceClient = currencyServiceClient;
    this.accountServiceClient = accountServiceClient;
    this.userServiceClient = userServiceGrpcClient;
    this.budgetServiceClient = budgetServiceClient;
    this.transactionEventPublisher = transactionEventPublisher;
    this.transferService = transferService;
    this.subscriptionService = subscriptionService;
    this.tagService = tagService;
  }

  @Override
  @Transactional
  public List<TransactionDto> createTransactions(TransactionRequestDto request) {
    List<Transaction> transactions =
        transactionRepository.saveAll(
            request.transactions().stream()
                .map(transaction -> createTransaction(request.userId(), transaction))
                .toList());
    transactions.forEach(
        transaction -> transactionEventPublisher.publishEvent(transaction, EventTypeEnum.CREATE));
    return TransactionMapper.toTransactionDto(transactions);
  }

  private Transaction createTransaction(UUID userId, TransactionRequestItemDto request) {
    accountServiceClient.checkIfAccountExists(request.accountId());
    Optional<BudgetDto> budget = Optional.empty();
    if (request.categoryId() != null) {
      budgetServiceClient.checkIfCategoryExists(request.categoryId());
      budget =
          budgetServiceClient.getBudgetByCategoryIdAndDate(request.categoryId(), request.date());
    }
    currencyServiceClient.checkIfCurrencyExists(request.currency());
    Set<Tag> tags = getTransactionTags(request.tags());
    LOGGER.info(
        "Creating transaction for account {} and category {}",
        request.accountId(),
        request.categoryId());
    Transaction transaction =
        tags.isEmpty()
            ? TransactionMapper.toTransaction(userId, request)
            : TransactionMapper.toTransaction(userId, request, tags);
    if (transaction == null) {
      return null;
    }
    transaction.setBudgetId(budget.map(BudgetDto::getBudgetId).orElse(null));
    transaction.setNormalizedAmount(
        currencyServiceClient.convertCurrencyAmountByUserPreference(
            userId, transaction.getAmount(), request.currency(), request.date()));
    return transaction;
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
    Transaction originalTransaction = new Transaction(transaction);
    if (request.accountId() != null) {
      accountServiceClient.checkIfAccountExists(request.accountId());
    }
    if (request.currency() != null) {
      currencyServiceClient.checkIfCurrencyExists(request.currency());
    }
    if (request.categoryId() != null) {
      budgetServiceClient.checkIfCategoryExists(request.categoryId());
      if (request.date() != null) {
        transaction.setBudgetId(
            budgetServiceClient
                .getBudgetByCategoryIdAndDate(request.categoryId(), request.date())
                .map(BudgetDto::getBudgetId)
                .orElse(null));
      }
    }
    Set<Tag> tags = getTransactionTags(request.tags());
    TransactionMapper.partialUpdate(transaction, request, tags);
    if (transaction.getAmount().compareTo(originalTransaction.getAmount()) != 0
        || !transaction.getCurrency().equalsIgnoreCase(originalTransaction.getCurrency())) {
      transaction.setNormalizedAmount(
          currencyServiceClient.convertCurrencyAmountByUserPreference(
              transaction.getUserId(),
              transaction.getAmount(),
              transaction.getCurrency(),
              transaction.getDate()));
    }
    transaction = transactionRepository.save(transaction);
    LOGGER.info(
        "Updated transaction: {} for user {}",
        transaction.getTransactionId(),
        transaction.getUserId());
    transactionEventPublisher.publishEvent(transaction, originalTransaction, EventTypeEnum.UPDATE);
    return TransactionMapper.toTransactionDto(transaction);
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
    transactionEventPublisher.publishEvent(transaction, EventTypeEnum.DELETE);
  }

  @Override
  @Transactional
  public void deleteAllTransactionsByUserId(UUID userId) {
    userServiceClient.checkIfUserStillExist(userId);
    transferService.deleteAllTransfersByUserId(userId);
    subscriptionService.deleteSubscriptionsByUserId(userId);
    tagService.deleteAllTagsByUserId(userId);
    LOGGER.info("Deleting transactions by userId {}", userId);
    transactionRepository.deleteAll(transactionRepository.findTransactionByUserId(userId));
  }

  @Override
  @Transactional
  public void deleteAllTransactionsByAccountId(UUID accountId) {
    accountServiceClient.checkIfAccountStillExists(accountId);
    transferService.deleteAllTransfersByAccountId(accountId);
    subscriptionService.deleteSubscriptionsByAccountId(accountId);
    LOGGER.info("Deleting transactions by account id {}", accountId);
    List<Transaction> transactions = transactionRepository.findTransactionByAccountId(accountId);
    transactionRepository.deleteAll(transactions);
    for (Transaction transaction : transactions) {
      transactionEventPublisher.publishEvent(transaction, EventTypeEnum.DELETE);
    }
  }

  @Override
  @Transactional
  public void removeCategoryFromTransactions(UUID categoryId) {
    budgetServiceClient.checkIfCategoryStillExists(categoryId);
    subscriptionService.removeCategoryFromAllSubscriptions(categoryId);
    LOGGER.info("Removing category {} from transactions", categoryId);
    List<Transaction> transactions = transactionRepository.findTransactionByCategoryId(categoryId);
    transactions.forEach(transaction -> transaction.setCategoryId(null));
    transactionRepository.saveAll(transactions);
    for (Transaction transaction : transactions) {
      transactionEventPublisher.publishEvent(transaction, EventTypeEnum.UPDATE);
    }
  }
}
