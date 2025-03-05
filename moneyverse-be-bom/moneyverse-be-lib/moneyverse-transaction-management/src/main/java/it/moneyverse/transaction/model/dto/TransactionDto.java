package it.moneyverse.transaction.model.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = TransactionDto.Builder.class)
@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator.class,
    property = "transactionId")
public class TransactionDto implements Serializable {

  private final UUID transactionId;
  private final UUID userId;
  private final UUID accountId;
  private final UUID categoryId;
  private final UUID budgetId;
  private final LocalDate date;
  private final String description;
  private final BigDecimal amount;
  private final BigDecimal normalizedAmount;
  private final String currency;
  private final Set<TagDto> tags;
  private final UUID transferId;
  private final UUID subscriptionId;

  public TransactionDto(Builder builder) {
    this.transactionId = builder.transactionId;
    this.userId = builder.userId;
    this.accountId = builder.accountId;
    this.categoryId = builder.categoryId;
    this.budgetId = builder.budgetId;
    this.date = builder.date;
    this.description = builder.description;
    this.amount = builder.amount;
    this.normalizedAmount = builder.normalizedAmount;
    this.currency = builder.currency;
    this.tags = builder.tags;
    this.transferId = builder.transferId;
    this.subscriptionId = builder.subscriptionId;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private UUID transactionId;
    private UUID userId;
    private UUID accountId;
    private UUID categoryId;
    private UUID budgetId;
    private LocalDate date;
    private String description;
    private BigDecimal amount;
    private BigDecimal normalizedAmount;
    private String currency;
    private Set<TagDto> tags;
    private UUID transferId;
    private UUID subscriptionId;

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

    public Builder withDate(LocalDate date) {
      this.date = date;
      return this;
    }

    public Builder withDescription(String description) {
      this.description = description;
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

    public Builder withTags(Set<TagDto> tags) {
      this.tags = tags;
      return this;
    }

    public Builder withTransferId(UUID transferId) {
      this.transferId = transferId;
      return this;
    }

    public Builder withSubscriptionId(UUID subscriptionId) {
      this.subscriptionId = subscriptionId;
      return this;
    }

    public TransactionDto build() {
      return new TransactionDto(this);
    }
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

  public LocalDate getDate() {
    return date;
  }

  public String getDescription() {
    return description;
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

  public Set<TagDto> getTags() {
    return tags;
  }

  public UUID getTransferId() {
    return transferId;
  }

  public UUID getSubscriptionId() {
    return subscriptionId;
  }
}
