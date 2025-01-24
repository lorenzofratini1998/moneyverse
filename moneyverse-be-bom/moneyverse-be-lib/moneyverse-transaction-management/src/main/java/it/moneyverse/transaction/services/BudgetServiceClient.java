package it.moneyverse.transaction.services;

import java.util.UUID;

public interface BudgetServiceClient {
  Boolean checkIfBudgetExists(UUID budgetId);
}
