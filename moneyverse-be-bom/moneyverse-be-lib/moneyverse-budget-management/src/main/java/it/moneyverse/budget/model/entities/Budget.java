package it.moneyverse.budget.model.entities;

import it.moneyverse.core.model.entities.Auditable;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(
    name = "BUDGETS",
    uniqueConstraints = {
      @UniqueConstraint(columnNames = {"CATEGORY_ID", "START_DATE", "END_DATE"})
    })
public class Budget extends Auditable implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "BUDGET_ID")
  private UUID budgetId;

  @ManyToOne(optional = false)
  @JoinColumn(name = "CATEGORY_ID", nullable = false)
  private Category category;

  @Column(name = "START_DATE", nullable = false)
  private LocalDate startDate;

  @Column(name = "END_DATE", nullable = false)
  private LocalDate endDate;

  @Column(name = "AMOUNT", nullable = false)
  @ColumnDefault(value = "0.0")
  private BigDecimal amount = BigDecimal.ZERO;

  @Column(name = "BUDGET_LIMIT", nullable = false)
  private BigDecimal budgetLimit;

  @Column(name = "CURRENCY", nullable = false, length = 3)
  private String currency;

  public UUID getBudgetId() {
    return budgetId;
  }

  public void setBudgetId(UUID budgetId) {
    this.budgetId = budgetId;
  }

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public LocalDate getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  public LocalDate getEndDate() {
    return endDate;
  }

  public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public BigDecimal getBudgetLimit() {
    return budgetLimit;
  }

  public void setBudgetLimit(BigDecimal budgetLimit) {
    this.budgetLimit = budgetLimit;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }
}
