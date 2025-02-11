package it.moneyverse.transaction.services;

import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.services.CurrencyServiceClient;
import it.moneyverse.core.services.UserServiceClient;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceClient {

  private final AccountServiceClient accountServiceClient;
  private final BudgetServiceClient budgetServiceClient;
  private final UserServiceClient userServiceClient;
  private final CurrencyServiceClient currencyServiceClient;

  public TransactionServiceClient(
      AccountServiceClient accountServiceClient,
      BudgetServiceClient budgetServiceClient,
      UserServiceClient userServiceClient,
      CurrencyServiceClient currencyServiceClient) {
    this.accountServiceClient = accountServiceClient;
    this.budgetServiceClient = budgetServiceClient;
    this.userServiceClient = userServiceClient;
    this.currencyServiceClient = currencyServiceClient;
  }

  public void checkIfAccountExists(UUID accountId) {
    if (Boolean.FALSE.equals(accountServiceClient.checkIfAccountExists(accountId))) {
      throw new ResourceNotFoundException("Account %s does not exists".formatted(accountId));
    }
  }

  public void checkIfCategoryExists(UUID categoryId) {
    if (Boolean.FALSE.equals(budgetServiceClient.checkIfCategoryExists(categoryId))) {
      throw new ResourceNotFoundException("Category %s does not exists".formatted(categoryId));
    }
  }

  public void checkIfCurrencyExists(String currency) {
    if (Boolean.FALSE.equals(currencyServiceClient.checkIfCurrencyExists(currency))) {
      throw new ResourceNotFoundException("Currency %s does not exists".formatted(currency));
    }
  }

  public void checkIfUserExists(UUID userId) {
    if (Boolean.FALSE.equals(userServiceClient.checkIfUserExists(userId))) {
      throw new ResourceNotFoundException("User %s does not exists".formatted(userId));
    }
  }
}
