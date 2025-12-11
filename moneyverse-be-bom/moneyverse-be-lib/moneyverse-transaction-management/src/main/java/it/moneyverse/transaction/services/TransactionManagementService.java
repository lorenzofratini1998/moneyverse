package it.moneyverse.transaction.services;

import it.moneyverse.core.enums.EventTypeEnum;
import it.moneyverse.core.enums.SortAttribute;
import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.model.dto.PageCriteria;
import it.moneyverse.core.model.dto.PageMetadataDto;
import it.moneyverse.core.model.dto.PagedResponseDto;
import it.moneyverse.core.model.dto.SortCriteria;
import it.moneyverse.core.services.CurrencyServiceClient;
import it.moneyverse.core.services.SecurityService;
import it.moneyverse.core.services.SseEventService;
import it.moneyverse.core.services.UserServiceClient;
import it.moneyverse.transaction.enums.TransactionSortAttributeEnum;
import it.moneyverse.transaction.enums.TransactionSseEventEnum;
import it.moneyverse.transaction.model.dto.*;
import it.moneyverse.transaction.model.entities.Tag;
import it.moneyverse.transaction.model.entities.Transaction;
import it.moneyverse.transaction.model.repositories.TransactionRepository;
import it.moneyverse.transaction.model.validator.TransactionValidator;
import it.moneyverse.transaction.runtime.messages.TransactionEventPublisher;
import it.moneyverse.transaction.utils.mapper.TransactionMapper;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionManagementService implements TransactionService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TransactionManagementService.class);

  private final TransactionRepository transactionRepository;
  private final CurrencyServiceClient currencyServiceClient;
  private final AccountServiceClient accountServiceClient;
  private final UserServiceClient userServiceClient;
  private final BudgetServiceClient budgetServiceClient;
  private final TransactionEventPublisher transactionEventPublisher;
  private final TransferService transferService;
  private final TransactionFactoryService transactionFactoryService;
  private final SubscriptionService subscriptionService;
  private final TagService tagService;
  private final TransactionValidator transactionValidator;
  private final SecurityService securityService;
  private final SseEventService eventService;

  public TransactionManagementService(
      TransactionRepository transactionRepository,
      CurrencyServiceClient currencyServiceClient,
      AccountServiceClient accountServiceClient,
      UserServiceClient userServiceGrpcClient,
      BudgetServiceClient budgetServiceClient,
      TransactionEventPublisher transactionEventPublisher,
      TransferService transferService,
      TransactionFactoryService transactionFactoryService,
      SubscriptionService subscriptionService,
      TagService tagService,
      TransactionValidator transactionValidator,
      SecurityService securityService,
      SseEventService eventService) {
    this.transactionRepository = transactionRepository;
    this.currencyServiceClient = currencyServiceClient;
    this.accountServiceClient = accountServiceClient;
    this.userServiceClient = userServiceGrpcClient;
    this.budgetServiceClient = budgetServiceClient;
    this.transactionEventPublisher = transactionEventPublisher;
    this.transferService = transferService;
    this.transactionFactoryService = transactionFactoryService;
    this.subscriptionService = subscriptionService;
    this.tagService = tagService;
    this.transactionValidator = transactionValidator;
    this.securityService = securityService;
    this.eventService = eventService;
  }

  @Override
  @Transactional
  public List<TransactionDto> createTransactions(TransactionRequestDto request) {
    List<Transaction> transactions =
        transactionRepository.saveAll(
            request.transactions().stream()
                .map(transaction -> createTransaction(request.userId(), transaction))
                .toList());
    transactionEventPublisher.publish(transactions, EventTypeEnum.CREATE);
    List<TransactionDto> result = TransactionMapper.toTransactionDto(transactions);
    eventService.publishEvent(
        securityService.getAuthenticatedUserId(),
        TransactionSseEventEnum.TRANSACTION_CREATED.name(),
        result);
    return result;
  }

  private Transaction createTransaction(UUID userId, TransactionRequestItemDto request) {
    transactionValidator.validate(request);
    return transactionFactoryService.createTransaction(userId, request);
  }

  @Override
  @Transactional(readOnly = true)
  public PagedResponseDto<TransactionDto> getTransactions(
      UUID userId, TransactionCriteria criteria) {
    if (criteria.getPage() == null) {
      PageCriteria pageCriteria = new PageCriteria();
      pageCriteria.setOffset(0);
      pageCriteria.setLimit(25);
      criteria.setPage(pageCriteria);
    }
    if (criteria.getSort() == null) {
      criteria.setSort(
          new SortCriteria<>(
              SortAttribute.getDefault(TransactionSortAttributeEnum.class), Sort.Direction.DESC));
    }
    LOGGER.info("Finding transactions with filters: {}", criteria);
    List<TransactionDto> transactions =
        TransactionMapper.toTransactionDto(
            transactionRepository.findTransactions(userId, criteria));
    long totalElements = transactionRepository.count(userId, criteria);
    return PagedResponseDto.<TransactionDto>builder()
        .withMetadata(
            PageMetadataDto.builder()
                .withTotalElements(totalElements)
                .withNumber(criteria.getPage().getOffset())
                .withSize(criteria.getPage().getLimit())
                .withTotalPages(
                    (int) Math.ceil(totalElements * 1.0 / criteria.getPage().getLimit()))
                .build())
        .withContent(transactions)
        .build();
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
    transactionValidator.validate(request);
    Transaction originalTransaction = transaction.copy();
    if (request.categoryId() != null && request.date() != null) {
      transaction.setBudgetId(
          budgetServiceClient.getBudgetId(request.categoryId(), request.date()));
    }
    Set<Tag> tags = null;
    if (request.tags() != null) {
      tags = tagService.getTagsByIds(request.tags());
    }
    TransactionMapper.partialUpdate(transaction, request, tags);
    if (request.amount() != null || request.currency() != null) {
      transaction.setNormalizedAmount(
          currencyServiceClient.convertCurrencyAmountByUserPreference(
              transaction.getUserId(),
              transaction.getAmount(),
              transaction.getCurrency(),
              transaction.getDate()));
    }
    transaction = transactionRepository.save(transaction);
    LOGGER.info(
        "Updated transaction {} for user {}",
        transaction.getTransactionId(),
        transaction.getUserId());
    transactionEventPublisher.publish(transaction, originalTransaction, EventTypeEnum.UPDATE);
    TransactionDto result = TransactionMapper.toTransactionDto(transaction);
    eventService.publishEvent(
        securityService.getAuthenticatedUserId(),
        TransactionSseEventEnum.TRANSACTION_UPDATED.name(),
        result);
    return result;
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
    transactionEventPublisher.publish(transaction, EventTypeEnum.DELETE);
    eventService.publishEvent(
        securityService.getAuthenticatedUserId(),
        TransactionSseEventEnum.TRANSACTION_DELETED.name(),
        transactionId);
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
    transactionEventPublisher.publish(transactions, EventTypeEnum.DELETE);
    eventService.publishEvent(
        securityService.getAuthenticatedUserId(),
        TransactionSseEventEnum.TRANSACTION_DELETED.name(),
        accountId);
  }

  @Override
  @Transactional
  public void removeCategoryFromTransactions(UUID categoryId) {
    budgetServiceClient.checkIfCategoryStillExists(categoryId);
    LOGGER.info("Removing category {} from transactions", categoryId);
    List<Transaction> transactions = transactionRepository.findTransactionByCategoryId(categoryId);
    transactions.forEach(
        transaction -> {
          transaction.setCategoryId(null);
          transaction.setBudgetId(null);
        });
    transactionRepository.saveAll(transactions);
    for (Transaction transaction : transactions) {
      transactionEventPublisher.publish(transaction, EventTypeEnum.UPDATE);
    }
    eventService.publishEvent(
        securityService.getAuthenticatedUserId(),
        TransactionSseEventEnum.TRANSACTION_UPDATED.name(),
        categoryId);
  }

  @Override
  @Transactional
  public void removeBudgetFromTransactions(UUID budgetId) {
    budgetServiceClient.checkIfBudgetStillExists(budgetId);
    LOGGER.info("Removing budget {} from transactions", budgetId);
    List<Transaction> transactions = transactionRepository.findTransactionByBudgetId(budgetId);
    transactions.forEach(transaction -> transaction.setBudgetId(null));
    transactionRepository.saveAll(transactions);
    for (Transaction transaction : transactions) {
      transactionEventPublisher.publish(transaction, EventTypeEnum.UPDATE);
    }
    eventService.publishEvent(
        securityService.getAuthenticatedUserId(),
        TransactionSseEventEnum.TRANSACTION_UPDATED.name(),
        budgetId);
  }
}
