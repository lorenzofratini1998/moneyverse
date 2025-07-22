package it.moneyverse.analytics.model.entities;

import it.moneyverse.core.enums.EventTypeEnum;
import it.moneyverse.analytics.enums.TransactionEventStateEnum;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "TRANSACTION_EVENTS_BUFFER")
public class TransactionEventBuffer {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "EVENT_ID")
  private UUID eventId;

  @Column(name = "EVENT_TYPE", nullable = false)
  @Enumerated(EnumType.STRING)
  private EventTypeEnum eventType;

  @Column(name = "USER_ID", nullable = false)
  private UUID userId;

  @Column(name = "TRANSACTION_ID", nullable = false)
  private UUID transactionId;

  @Column(name = "ORIGINAL_TRANSACTION_ID")
  private UUID originalTransactionId;

  @Column(name = "ACCOUNT_ID", nullable = false)
  private UUID accountId;

  @Column(name = "CATEGORY_ID")
  private UUID categoryId;

  @Column(name = "BUDGET_ID")
  private UUID budgetId;

  @Column(name = "AMOUNT", nullable = false)
  private BigDecimal amount;

  @Column(name = "NORMALIZED_AMOUNT", nullable = false)
  private BigDecimal normalizedAmount;

  @Column(name = "CURRENCY", nullable = false)
  private String currency;

  @Column(name = "DATE", nullable = false)
  private LocalDate date;

  @Column(name = "EVENT_TIMESTAMP", nullable = false)
  private LocalDateTime eventTimestamp;

  @Column(name = "STATE", nullable = false)
  @Enumerated(EnumType.STRING)
  private TransactionEventStateEnum state = TransactionEventStateEnum.PENDING;

  public UUID getEventId() {
    return eventId;
  }

  public void setEventId(UUID eventId) {
    this.eventId = eventId;
  }

  public EventTypeEnum getEventType() {
    return eventType;
  }

  public void setEventType(EventTypeEnum eventType) {
    this.eventType = eventType;
  }

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public UUID getTransactionId() {
    return transactionId;
  }

  public void setTransactionId(UUID transactionId) {
    this.transactionId = transactionId;
  }

  public UUID getOriginalTransactionId() {
    return originalTransactionId;
  }

  public void setOriginalTransactionId(UUID originalTransactionId) {
    this.originalTransactionId = originalTransactionId;
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

  public UUID getBudgetId() {
    return budgetId;
  }

  public void setBudgetId(UUID budgetId) {
    this.budgetId = budgetId;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public BigDecimal getNormalizedAmount() {
    return normalizedAmount;
  }

  public void setNormalizedAmount(BigDecimal normalizedAmount) {
    this.normalizedAmount = normalizedAmount;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public LocalDateTime getEventTimestamp() {
    return eventTimestamp;
  }

  public void setEventTimestamp(LocalDateTime eventDateTime) {
    this.eventTimestamp = eventDateTime;
  }

  public TransactionEventStateEnum getState() {
    return state;
  }

  public void setState(TransactionEventStateEnum state) {
    this.state = state;
  }
}
