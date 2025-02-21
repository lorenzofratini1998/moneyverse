package it.moneyverse.transaction.model.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = SubscriptionDto.Builder.class)
@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator.class,
    property = "subscriptionId")
public class SubscriptionDto {
  private final UUID subscriptionId;
  private final UUID userId;
  private final UUID accountId;
  private final UUID categoryId;
  private final BigDecimal amount;
  private final BigDecimal totalAmount;
  private final String currency;
  private final String subscriptionName;
  private final String recurrenceRule;
  private final LocalDate startDate;
  private final LocalDate endDate;
  private final LocalDate nextExecutionDate;
  private final Boolean isActive;
  private final List<TransactionDto> transactions;

  public SubscriptionDto(Builder builder) {
    this.subscriptionId = builder.subscriptionId;
    this.userId = builder.userId;
    this.accountId = builder.accountId;
    this.categoryId = builder.categoryId;
    this.amount = builder.amount;
    this.totalAmount = builder.totalAmount;
    this.currency = builder.currency;
    this.subscriptionName = builder.subscriptionName;
    this.recurrenceRule = builder.recurrenceRule;
    this.startDate = builder.startDate;
    this.endDate = builder.endDate;
    this.nextExecutionDate = builder.nextExecutionDate;
    this.isActive = builder.isActive;
    this.transactions = builder.transactions;
  }

  public UUID getSubscriptionId() {
    return subscriptionId;
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

  public BigDecimal getAmount() {
    return amount;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public String getCurrency() {
    return currency;
  }

  public String getSubscriptionName() {
    return subscriptionName;
  }

  public String getRecurrenceRule() {
    return recurrenceRule;
  }

  public LocalDate getStartDate() {
    return startDate;
  }

  public LocalDate getEndDate() {
    return endDate;
  }

  public LocalDate getNextExecutionDate() {
    return nextExecutionDate;
  }

  public Boolean getActive() {
    return isActive;
  }

  public List<TransactionDto> getTransactions() {
    return transactions;
  }

  public static class Builder {
    private UUID subscriptionId;
    private UUID userId;
    private UUID accountId;
    private UUID categoryId;
    private BigDecimal amount;
    private BigDecimal totalAmount;
    private String currency;
    private String subscriptionName;
    private String recurrenceRule;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate nextExecutionDate;
    private Boolean isActive;
    private List<TransactionDto> transactions;

    public Builder withSubscriptionId(UUID subscriptionId) {
      this.subscriptionId = subscriptionId;
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

    public Builder withAmount(BigDecimal amount) {
      this.amount = amount;
      return this;
    }

    public Builder withTotalAmount(BigDecimal totalAmount) {
      this.totalAmount = totalAmount;
      return this;
    }

    public Builder withCurrency(String currency) {
      this.currency = currency;
      return this;
    }

    public Builder withSubscriptionName(String subscriptionName) {
      this.subscriptionName = subscriptionName;
      return this;
    }

    public Builder withRecurrenceRule(String recurrenceRule) {
      this.recurrenceRule = recurrenceRule;
      return this;
    }

    public Builder withStartDate(LocalDate startDate) {
      this.startDate = startDate;
      return this;
    }

    public Builder withEndDate(LocalDate endDate) {
      this.endDate = endDate;
      return this;
    }

    public Builder withNextExecutionDate(LocalDate nextExecutionDate) {
      this.nextExecutionDate = nextExecutionDate;
      return this;
    }

    public Builder withActive(Boolean isActive) {
      this.isActive = isActive;
      return this;
    }

    public Builder withTransactions(List<TransactionDto> transactions) {
      this.transactions = transactions;
      return this;
    }

    public SubscriptionDto build() {
      return new SubscriptionDto(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
