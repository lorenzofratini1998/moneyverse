package it.moneyverse.core.model.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import it.moneyverse.core.enums.EventTypeEnum;
import jakarta.json.bind.annotation.JsonbCreator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = TransactionEvent.Builder.class)
public class TransactionEvent extends AbstractEvent {
  private final UUID transactionId;
  private final UUID userId;
  private final UUID accountId;
  private final UUID categoryId;
  private final UUID budgetId;
  private final Set<UUID> tags;
  private final BigDecimal amount;
  private final BigDecimal normalizedAmount;
  private final String currency;
  private final LocalDate date;
  private final LocalDateTime eventTimestamp;
  private final TransactionEvent previousTransaction;

  @JsonbCreator
  public TransactionEvent(Builder builder) {
    super(builder);
    this.transactionId = builder.transactionId;
    this.userId = builder.userId;
    this.accountId = builder.accountId;
    this.categoryId = builder.categoryId;
    this.budgetId = builder.budgetId;
    this.tags = builder.tags;
    this.amount = builder.amount;
    this.normalizedAmount = builder.normalizedAmount;
    this.currency = builder.currency;
    this.date = builder.date;
    this.eventTimestamp = builder.eventTimestamp;
    this.previousTransaction = builder.previousTransaction;
  }

  public static class Builder extends AbstractBuilder<TransactionEvent, Builder> {
    private UUID transactionId;
    private UUID userId;
    private UUID accountId;
    private UUID categoryId;
    private UUID budgetId;
    private Set<UUID> tags;
    private BigDecimal amount;
    private BigDecimal normalizedAmount;
    private String currency;
    private LocalDate date;
    private LocalDateTime eventTimestamp;
    private TransactionEvent previousTransaction;
    private EventTypeEnum eventType;

    public Builder withTransactionId(UUID transactionId) {
      this.transactionId = transactionId;
      return this;
    }

    public Builder withUserId(UUID userId) {
      this.userId = userId;
      return this;
    }

    public Builder withAccountId(UUID accountId) {
      this.accountId = accountId;
      return this;
    }

    public Builder withCategoryId(UUID categoryId) {
      this.categoryId = categoryId;
      return this;
    }

    public Builder withBudgetId(UUID budgetId) {
      this.budgetId = budgetId;
      return this;
    }

    public Builder withTags(Set<UUID> tags) {
      this.tags = tags;
      return this;
    }

    public Builder withAmount(BigDecimal amount) {
      this.amount = amount;
      return this;
    }

    public Builder withNormalizedAmount(BigDecimal normalizedAmount) {
      this.normalizedAmount = normalizedAmount;
      return this;
    }

    public Builder withCurrency(String currency) {
      this.currency = currency;
      return this;
    }

    public Builder withDate(LocalDate date) {
      this.date = date;
      return this;
    }

    public Builder withEventTimestamp(LocalDateTime eventTimestamp) {
      this.eventTimestamp = eventTimestamp;
      return this;
    }

    public Builder withPreviousTransaction(TransactionEvent previousTransaction) {
      this.previousTransaction = previousTransaction;
      return this;
    }

    @Override
    protected Builder self() {
      return this;
    }

    @Override
    public TransactionEvent build() {
      return new TransactionEvent(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public UUID key() {
    return transactionId;
  }

  public UUID getTransactionId() {
    return transactionId;
  }

  public UUID getUserId() {
    return userId;
  }

  public UUID getAccountId() {
    return accountId;
  }

  public UUID getCategoryId() {
    return categoryId;
  }

  public UUID getBudgetId() {
    return budgetId;
  }

  public Set<UUID> getTags() {
    return tags;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public BigDecimal getNormalizedAmount() {
    return normalizedAmount;
  }

  public String getCurrency() {
    return currency;
  }

  public LocalDate getDate() {
    return date;
  }

  public LocalDateTime getEventTimestamp() {
    return eventTimestamp;
  }

  public TransactionEvent getPreviousTransaction() {
    return previousTransaction;
  }
}
