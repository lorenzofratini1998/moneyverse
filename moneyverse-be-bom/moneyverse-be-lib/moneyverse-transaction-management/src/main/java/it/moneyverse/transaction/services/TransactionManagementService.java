package it.moneyverse.transaction.services;

import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.transaction.model.dto.TransactionDto;
import it.moneyverse.transaction.model.dto.TransactionRequestDto;
import it.moneyverse.transaction.model.entities.Transaction;
import it.moneyverse.transaction.model.repositories.TagRepository;
import it.moneyverse.transaction.model.repositories.TransactionRepository;
import it.moneyverse.transaction.utils.mapper.TransactionMapper;
import java.util.UUID;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
