package it.moneyverse.core.model.events;

import it.moneyverse.core.enums.EventTypeEnum;
import it.moneyverse.core.utils.JsonUtils;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.util.ReflectionUtils;

public class TransactionEvent implements MessageEvent<UUID, String> {
  private UUID transactionId;
  private UUID accountId;
  private UUID previousAccountId;
  private UUID categoryId;
  private UUID previousCategoryId;
  private UUID budgetId;
  private BigDecimal amount;
  private BigDecimal normalizedAmount;
  private BigDecimal previousAmount;
  private String currency;
  private LocalDate date;
  private EventTypeEnum eventType;

  public UUID getTransactionId() {
    return transactionId;
  }

  public void setTransactionId(UUID transactionId) {
    this.transactionId = transactionId;
  }

  public UUID getAccountId() {
    return accountId;
  }

  public void setAccountId(UUID accountId) {
    this.accountId = accountId;
  }

  public UUID getPreviousAccountId() {
    return previousAccountId;
  }

  public void setPreviousAccountId(UUID previousAccountId) {
    this.previousAccountId = previousAccountId;
  }

  public UUID getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(UUID categoryId) {
    this.categoryId = categoryId;
  }

  public UUID getPreviousCategoryId() {
    return previousCategoryId;
  }

  public void setPreviousCategoryId(UUID previousCategoryId) {
    this.previousCategoryId = previousCategoryId;
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

  public BigDecimal getPreviousAmount() {
    return previousAmount;
  }

  public void setPreviousAmount(BigDecimal previousAmount) {
    this.previousAmount = previousAmount;
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

  public EventTypeEnum getEventType() {
    return eventType;
  }

  public void setEventType(EventTypeEnum eventType) {
    this.eventType = eventType;
  }

  @Override
  public UUID key() {
    return transactionId;
  }

  @Override
  public String value() {
    Map<String, Object> payload = new HashMap<>();
    for (Field field : this.getClass().getDeclaredFields()) {
      ReflectionUtils.makeAccessible(field);
      payload.put(field.getName(), ReflectionUtils.getField(field, this));
    }
    return JsonUtils.toJson(payload);
  }

  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }
}
