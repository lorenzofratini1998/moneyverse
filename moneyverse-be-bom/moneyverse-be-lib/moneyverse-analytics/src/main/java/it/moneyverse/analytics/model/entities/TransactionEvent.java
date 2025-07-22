package it.moneyverse.analytics.model.entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "TRANSACTION_EVENTS")
public class TransactionEvent implements Serializable {

  public static final class Columns {
    public static final String EVENT_ID = "EVENT_ID";
    public static final String EVENT_TYPE = "EVENT_TYPE";
    public static final String USER_ID = "USER_ID";
    public static final String TRANSACTION_ID = "TRANSACTION_ID";
    public static final String ORIGINAL_TRANSACTION_ID = "ORIGINAL_TRANSACTION_ID";
    public static final String ACCOUNT_ID = "ACCOUNT_ID";
    public static final String CATEGORY_ID = "CATEGORY_ID";
    public static final String BUDGET_ID = "BUDGET_ID";
    public static final String TAGS = "TAGS";
    public static final String AMOUNT = "AMOUNT";
    public static final String NORMALIZED_AMOUNT = "NORMALIZED_AMOUNT";
    public static final String CURRENCY = "CURRENCY";
    public static final String DATE = "DATE";
    public static final String EVENT_TIMESTAMP = "EVENT_TIMESTAMP";

    private Columns() {}
  }

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = Columns.EVENT_ID, nullable = false)
  private UUID eventId;

  @Column(name = Columns.EVENT_TYPE, nullable = false)
  private Integer eventType;

  @Column(name = Columns.USER_ID, nullable = false)
  private UUID userId;

  @Column(name = Columns.TRANSACTION_ID, nullable = false)
  private UUID transactionId;

  @Column(name = Columns.ORIGINAL_TRANSACTION_ID)
  private UUID originalTransactionId;

  @Column(name = Columns.ACCOUNT_ID, nullable = false)
  private UUID accountId;

  @Column(name = Columns.CATEGORY_ID)
  private UUID categoryId;

  @Column(name = Columns.BUDGET_ID)
  private UUID budgetId;

  @Column(name = Columns.TAGS)
  private List<UUID> tags;

  @Column(name = Columns.AMOUNT, nullable = false)
  private BigDecimal amount;

  @Column(name = Columns.NORMALIZED_AMOUNT, nullable = false)
  private BigDecimal normalizedAmount;

  @Column(name = Columns.CURRENCY, nullable = false)
  private String currency;

  @Column(name = Columns.DATE, nullable = false)
  private LocalDate date;

  @Column(name = Columns.EVENT_TIMESTAMP, nullable = false)
  private LocalDateTime eventTimestamp;

  public UUID getEventId() {
    return eventId;
  }

  public void setEventId(UUID eventId) {
    this.eventId = eventId;
  }

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public Integer getEventType() {
    return eventType;
  }

  public void setEventType(Integer eventType) {
    this.eventType = eventType;
  }

  public UUID getAccountId() {
    return accountId;
  }

  public void setAccountId(UUID accountId) {
    this.accountId = accountId;
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

  public List<UUID> getTags() {
    return tags;
  }

  public void setTags(List<UUID> tags) {
    this.tags = tags;
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
}
