package it.moneyverse.transaction.model.validator;

import it.moneyverse.core.services.CurrencyServiceClient;
import it.moneyverse.transaction.exceptions.AccountTransferException;
import it.moneyverse.transaction.model.dto.*;
import it.moneyverse.transaction.services.AccountServiceClient;
import it.moneyverse.transaction.services.BudgetServiceClient;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class TransactionValidator {

  private final CurrencyServiceClient currencyServiceClient;
  private final AccountServiceClient accountServiceClient;
  private final BudgetServiceClient budgetServiceClient;

  public TransactionValidator(
      CurrencyServiceClient currencyServiceClient,
      AccountServiceClient accountServiceClient,
      BudgetServiceClient budgetServiceClient) {
    this.currencyServiceClient = currencyServiceClient;
    this.accountServiceClient = accountServiceClient;
    this.budgetServiceClient = budgetServiceClient;
  }

  public void validate(TransactionRequestItemDto request) {
    accountServiceClient.checkIfAccountExists(request.accountId());
    currencyServiceClient.checkIfCurrencyExists(request.currency());
    if (request.categoryId() != null) {
      budgetServiceClient.checkIfCategoryExists(request.categoryId());
    }
  }

  public void validate(TransactionUpdateRequestDto request) {
    if (request.accountId() != null) {
      accountServiceClient.checkIfAccountExists(request.accountId());
    }
    if (request.currency() != null) {
      currencyServiceClient.checkIfCurrencyExists(request.currency());
    }
    if (request.categoryId() != null) {
      budgetServiceClient.checkIfCategoryExists(request.categoryId());
    }
  }

  public void validate(TransferRequestDto request) {
    checkSourceAndDestinationAccounts(request.fromAccount(), request.toAccount());
    accountServiceClient.checkIfAccountExists(request.fromAccount());
    accountServiceClient.checkIfAccountExists(request.toAccount());
    currencyServiceClient.checkIfCurrencyExists(request.currency());
  }

  public void validate(TransferUpdateRequestDto request) {
    if (request.currency() != null) {
      currencyServiceClient.checkIfCurrencyExists(request.currency());
    }
    if (request.fromAccount() != null && request.toAccount() != null) {
      checkSourceAndDestinationAccounts(request.fromAccount(), request.toAccount());
    }
  }

  private void checkSourceAndDestinationAccounts(UUID sourceAccountId, UUID destinationAccountId) {
    if (sourceAccountId.equals(destinationAccountId)) {
      throw new AccountTransferException("The source and destination accounts must be different.");
    }
  }

  public void validate(SubscriptionRequestDto request) {
    accountServiceClient.checkIfAccountExists(request.accountId());
    if (request.categoryId() != null) {
      budgetServiceClient.checkIfCategoryExists(request.categoryId());
    }
    currencyServiceClient.checkIfCurrencyExists(request.currency());
  }

  public void validate(SubscriptionUpdateRequestDto request) {
    if (request.accountId() != null) {
      accountServiceClient.checkIfAccountExists(request.accountId());
    }
    if (request.categoryId() != null) {
      budgetServiceClient.checkIfCategoryExists(request.categoryId());
    }
    if (request.currency() != null) {
      currencyServiceClient.checkIfCurrencyExists(request.currency());
    }
  }
}
