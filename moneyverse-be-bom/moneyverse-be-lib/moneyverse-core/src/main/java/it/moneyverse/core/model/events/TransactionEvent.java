package it.moneyverse.core.model.events;

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
  private UUID categoryId;
  private UUID budgetId;
  private BigDecimal amount;
  private BigDecimal previousAmount;
  private LocalDate date;

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

  public BigDecimal getPreviousAmount() {
    return previousAmount;
  }

  public void setPreviousAmount(BigDecimal previousAmount) {
    this.previousAmount = previousAmount;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
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
}
