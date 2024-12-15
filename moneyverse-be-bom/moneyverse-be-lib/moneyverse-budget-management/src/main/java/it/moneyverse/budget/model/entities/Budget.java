package it.moneyverse.budget.model.entities;

import it.moneyverse.core.model.entities.Auditable;
import it.moneyverse.core.model.entities.BudgetModel;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(
    name = "BUDGETS",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"USERNAME", "BUDGET_NAME"})})
public class Budget extends Auditable implements Serializable, BudgetModel {

  @Serial private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "BUDGET_ID")
  private UUID budgetId;

  @Column(name = "USERNAME", nullable = false)
  private String username;

  @Column(name = "BUDGET_NAME", nullable = false)
  private String budgetName;

  @Column(name = "BUDGET_DESCRIPTION")
  private String description;

  @Column(name = "BUDGET_LIMIT")
  private BigDecimal budgetLimit;

  @Column(name = "AMOUNT", nullable = false)
  @ColumnDefault(value = "0.0")
  private BigDecimal amount;

  @Override
  public UUID getBudgetId() {
    return budgetId;
  }

  public void setBudgetId(UUID budgetId) {
    this.budgetId = budgetId;
  }

  @Override
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Override
  public String getBudgetName() {
    return budgetName;
  }

  public void setBudgetName(String budgetName) {
    this.budgetName = budgetName;
  }

  @Override
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public BigDecimal getBudgetLimit() {
    return budgetLimit;
  }

  public void setBudgetLimit(BigDecimal budgetLimit) {
    this.budgetLimit = budgetLimit;
  }

  @Override
  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }
}
