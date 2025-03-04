package it.moneyverse.transaction.services;

import it.moneyverse.core.services.CurrencyServiceClient;
import it.moneyverse.transaction.model.dto.TransactionRequestItemDto;
import it.moneyverse.transaction.model.entities.Subscription;
import it.moneyverse.transaction.model.entities.Tag;
import it.moneyverse.transaction.model.entities.Transaction;
import it.moneyverse.transaction.model.factories.TransactionFactory;
import it.moneyverse.transaction.utils.mapper.TransactionMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TransactionFactoryService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TransactionFactoryService.class);
  private final BudgetServiceClient budgetServiceClient;
  private final CurrencyServiceClient currencyServiceClient;
  private final TagService tagService;

  public TransactionFactoryService(
      BudgetServiceClient budgetServiceClient,
      CurrencyServiceClient currencyServiceClient,
      TagService tagService) {
    this.budgetServiceClient = budgetServiceClient;
    this.currencyServiceClient = currencyServiceClient;
    this.tagService = tagService;
  }

  public Transaction createTransaction(UUID userId, TransactionRequestItemDto request) {
    UUID budgetId = budgetServiceClient.getBudgetId(request.categoryId(), request.date());
    Set<Tag> tags = tagService.getTagsByIds(request.tags());
    LOGGER.info(
        "Creating transaction for account {} and category {}",
        request.accountId(),
        request.categoryId());
    Transaction transaction = TransactionMapper.toTransaction(userId, request, tags);
    if (transaction == null) {
      return null;
    }
    transaction.setBudgetId(budgetId);
    transaction.setNormalizedAmount(
        currencyServiceClient.convertCurrencyAmountByUserPreference(
            userId, request.amount(), request.currency(), request.date()));
    return transaction;
  }

  public Transaction createTransaction(Subscription subscription, LocalDate date) {
    UUID budgetId = budgetServiceClient.getBudgetId(subscription.getCategoryId(), date);
    BigDecimal normalizedAmount =
        currencyServiceClient.convertCurrencyAmountByUserPreference(
            subscription.getUserId(), subscription.getAmount(), subscription.getCurrency(), date);
    return TransactionFactory.createTransaction(subscription, date, budgetId, normalizedAmount);
  }
}
