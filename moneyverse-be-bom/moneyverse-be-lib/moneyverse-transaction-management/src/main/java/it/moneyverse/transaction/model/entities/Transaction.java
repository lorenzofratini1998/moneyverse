package it.moneyverse.transaction.model.entities;

import it.moneyverse.core.enums.CurrencyEnum;
import it.moneyverse.core.model.entities.Auditable;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "TRANSACTIONS")
public class Transaction extends Auditable implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "TRANSACTION_ID")
  private UUID transactionId;

  @Column(name = "USERNAME", nullable = false)
  private String username;

  @Column(name = "ACCOUNT_ID", nullable = false)
  private UUID accountId;

  @Column(name = "BUDGET_ID", nullable = false)
  private UUID budgetId;

  @Column(name = "DATE", nullable = false)
  private LocalDate date;

  @Column(name = "DESCRIPTION", nullable = false)
  private String description;

  @Column(name = "AMOUNT", nullable = false)
  private BigDecimal amount;

  @Column(name = "CURRENCY", nullable = false, length = 3)
  @Enumerated(EnumType.STRING)
  private CurrencyEnum currency;

  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(
      name = "TRANSACTIONS_TAGS",
      joinColumns = @JoinColumn(name = "TRANSACTION_ID"),
      inverseJoinColumns = @JoinColumn(name = "TAG_ID"))
  private Set<Tag> tags = new HashSet<>();

  public void addTag(Tag tag) {
    tags.add(tag);
    tag.getTransactions().add(this);
  }

  public void removeTag(Tag tag) {
    tags.remove(tag);
    tag.getTransactions().remove(this);
  }

  public UUID getTransactionId() {
    return transactionId;
  }

  public void setTransactionId(UUID transactionId) {
    this.transactionId = transactionId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public UUID getAccountId() {
    return accountId;
  }

  public void setAccountId(UUID accountId) {
    this.accountId = accountId;
  }

  public UUID getBudgetId() {
    return budgetId;
  }

  public void setBudgetId(UUID budgetId) {
    this.budgetId = budgetId;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public CurrencyEnum getCurrency() {
    return currency;
  }

  public void setCurrency(CurrencyEnum currency) {
    this.currency = currency;
  }

  public Set<Tag> getTags() {
    return tags;
  }

  public void setTags(Set<Tag> tags) {
    this.tags = tags;
  }
}
