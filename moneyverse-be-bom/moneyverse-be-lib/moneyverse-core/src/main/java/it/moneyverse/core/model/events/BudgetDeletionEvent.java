package it.moneyverse.core.model.events;

import java.util.UUID;

public class BudgetDeletionEvent implements MessageEvent<UUID, String> {
  private UUID budgetId;

  public BudgetDeletionEvent() {}

  public BudgetDeletionEvent(UUID budgetId) {
    this.budgetId = budgetId;
  }

  public UUID getBudgetId() {
    return budgetId;
  }

  public void setBudgetId(UUID budgetId) {
    this.budgetId = budgetId;
  }

  @Override
  public UUID key() {
    return budgetId;
  }

  @Override
  public String value() {
    return budgetId.toString();
  }
}
