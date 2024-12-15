package it.moneyverse.core.model.entities;

import java.math.BigDecimal;
import java.util.UUID;

public interface BudgetModel {

  UUID getBudgetId();

  String getUsername();

  String getBudgetName();

  String getDescription();

  BigDecimal getBudgetLimit();

  BigDecimal getAmount();
}
