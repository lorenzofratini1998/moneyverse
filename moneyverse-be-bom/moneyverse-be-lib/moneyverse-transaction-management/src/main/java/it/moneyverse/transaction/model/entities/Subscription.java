package it.moneyverse.transaction.model.entities;

import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.model.entities.Auditable;
import it.moneyverse.core.model.entities.Copyable;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "SUBSCRIPTIONS")
public class Subscription extends Auditable implements Serializable, Copyable<Subscription> {
  @Serial private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "SUBSCRIPTION_ID")
  private UUID subscriptionId;

  @Column(name = "USER_ID", nullable = false)
  private UUID userId;

  @Column(name = "ACCOUNT_ID", nullable = false)
  private UUID accountId;

  @Column(name = "CATEGORY_ID")
  private UUID categoryId;

  @Column(name = "AMOUNT", nullable = false, precision = 18, scale = 2)
  private BigDecimal amount;

  @Column(name = "TOTAL_AMOUNT", nullable = false, precision = 18, scale = 2)
  @ColumnDefault(value = "0.0")
  private BigDecimal totalAmount = BigDecimal.ZERO;

  @Column(name = "CURRENCY", nullable = false)
  private String currency;

  @Column(name = "SUBSCRIPTION_NAME", nullable = false)
  private String subscriptionName;

  @Column(name = "RECURRENCE_RULE", nullable = false)
  private String recurrenceRule;

  @Column(name = "START_DATE", nullable = false)
  private LocalDate startDate;

  @Column(name = "END_DATE")
  private LocalDate endDate;

  @Column(name = "NEXT_EXECUTION_DATE")
  private LocalDate nextExecutionDate;

  @Column(name = "IS_ACTIVE")
  @ColumnDefault(value = "TRUE")
  private Boolean isActive = true;

  @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Transaction> transactions = new ArrayList<>();

  public Subscription() {}

  private Subscription(Subscription source) {
    super(source);
    if (source != null) {
      this.subscriptionId = source.subscriptionId;
      this.userId = source.userId;
      this.accountId = source.accountId;
      this.categoryId = source.categoryId;
      this.amount = source.amount;
      this.totalAmount = source.totalAmount;
      this.currency = source.currency;
      this.subscriptionName = source.subscriptionName;
      this.recurrenceRule = source.recurrenceRule;
      this.startDate = source.startDate;
      this.endDate = source.endDate;
      this.nextExecutionDate = source.nextExecutionDate;
      this.isActive = source.isActive;
      this.transactions = source.transactions;
    }
  }

  @Override
  public Subscription copy() {
    Subscription copy = new Subscription(this);

    if (this.transactions != null) {
      copy.transactions =
          this.transactions.stream()
              .map(Transaction::copy)
              .collect(Collectors.toCollection(ArrayList::new));
    }

    return copy;
  }

  public void addTransaction(Transaction transaction) {
    transactions.add(transaction);
    transaction.setSubscription(this);
  }

  public void removeTransaction(Transaction transaction) {
    transactions.remove(transaction);
    transaction.setSubscription(null);
  }

  public Transaction getTransaction(UUID transactionId) {
    return transactions.stream()
        .filter(transaction -> transaction.getTransactionId().equals(transactionId))
        .findFirst()
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    "Transaction with id %s not found".formatted(transactionId)));
  }

  public UUID getSubscriptionId() {
    return subscriptionId;
  }

  public void setSubscriptionId(UUID subscriptionId) {
    this.subscriptionId = subscriptionId;
  }

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public UUID getAccountId() {
    return accountId;
  }

  public void setAccountId(UUID accountId) {
    this.accountId = accountId;
  }

  public UUID getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(UUID categoryId) {
    this.categoryId = categoryId;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(BigDecimal totalAmount) {
    this.totalAmount = totalAmount;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public String getSubscriptionName() {
    return subscriptionName;
  }

  public void setSubscriptionName(String subscriptionName) {
    this.subscriptionName = subscriptionName;
  }

  public String getRecurrenceRule() {
    return recurrenceRule;
  }

  public void setRecurrenceRule(String recurrenceRule) {
    this.recurrenceRule = recurrenceRule;
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

  public LocalDate getNextExecutionDate() {
    return nextExecutionDate;
  }

  public void setNextExecutionDate(LocalDate nextExecutionDate) {
    this.nextExecutionDate = nextExecutionDate;
  }

  public Boolean isActive() {
    return isActive;
  }

  public void setActive(Boolean active) {
    isActive = active;
  }

  public List<Transaction> getTransactions() {
    return transactions;
  }

  public void setTransactions(List<Transaction> transactions) {
    this.transactions = transactions;
  }
}
