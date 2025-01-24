package it.moneyverse.transaction.services;

import it.moneyverse.core.enums.SortAttribute;
import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.model.dto.PageCriteria;
import it.moneyverse.core.model.dto.SortCriteria;
import it.moneyverse.transaction.enums.TransactionSortAttributeEnum;
import it.moneyverse.transaction.model.dto.TransactionCriteria;
import it.moneyverse.transaction.model.dto.TransactionDto;
import it.moneyverse.transaction.model.dto.TransactionRequestDto;
import it.moneyverse.transaction.model.dto.TransactionUpdateRequestDto;
import it.moneyverse.transaction.model.entities.Transaction;
import it.moneyverse.transaction.model.repositories.TagRepository;
import it.moneyverse.transaction.model.repositories.TransactionRepository;
import it.moneyverse.transaction.utils.mapper.TransactionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Service
public class TransactionManagementService implements TransactionService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TransactionManagementService.class);

  private final AccountServiceClient accountServiceClient;
  private final BudgetServiceClient budgetServiceClient;
  private final TransactionRepository transactionRepository;
  private final TagRepository tagRepository;

  public TransactionManagementService(
      AccountServiceClient accountServiceClient,
      BudgetServiceClient budgetServiceClient,
      TransactionRepository transactionRepository,
      TagRepository tagRepository) {
    this.accountServiceClient = accountServiceClient;
    this.budgetServiceClient = budgetServiceClient;
    this.transactionRepository = transactionRepository;
    this.tagRepository = tagRepository;
  }

  @Override
  @Transactional
  public TransactionDto createTransaction(TransactionRequestDto request) {
    checkIfResourceExists(
        request.accountId(), accountServiceClient::checkIfAccountExists, "Account");
    checkIfResourceExists(request.budgetId(), budgetServiceClient::checkIfBudgetExists, "Budget");
    LOGGER.info(
        "Creating transaction for account {} and budget {}",
        request.accountId(),
        request.budgetId());
    Transaction transaction = TransactionMapper.toTransaction(request, tagRepository);
    TransactionDto result =
        TransactionMapper.toTransactionDto(transactionRepository.save(transaction));
    LOGGER.info(
        "Transaction created for account {} and budget {}",
        request.accountId(),
        request.budgetId());
    return result;
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
  public List<TransactionDto> getTransactions(TransactionCriteria criteria) {
    if (criteria.getPage() == null) {
      criteria.setPage(new PageCriteria());
    }
    if (criteria.getSort() == null) {
      criteria.setSort(
          new SortCriteria<>(
              SortAttribute.getDefault(TransactionSortAttributeEnum.class), Sort.Direction.DESC));
    }
    LOGGER.info("Finding transactions with filters: {}", criteria);
    return TransactionMapper.toTransactionDto(transactionRepository.findTransactions(criteria));
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
    transaction = TransactionMapper.partialUpdate(transaction, request, tagRepository);
    TransactionDto result =
        TransactionMapper.toTransactionDto(transactionRepository.save(transaction));
    LOGGER.info(
        "Updated transaction: {} for user {}", result.getTransactionId(), result.getUsername());
    return result;
  }

  @Override
  @Transactional
  public void deleteTransaction(UUID transactionId) {
    Transaction transaction = findTransactionById(transactionId);
    transactionRepository.delete(transaction);
    LOGGER.info(
        "Deleted transaction: {} for user {}", transaction.getTransactionId(), transaction.getUsername());
  }
}
