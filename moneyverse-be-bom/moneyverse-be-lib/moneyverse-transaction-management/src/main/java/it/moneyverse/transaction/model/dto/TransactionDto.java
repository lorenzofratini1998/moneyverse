package it.moneyverse.transaction.model.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import it.moneyverse.core.enums.CurrencyEnum;
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
  private final String username;
  private final UUID accountId;
  private final UUID budgetId;
  private final LocalDate date;
  private final String description;
  private final BigDecimal amount;
  private final CurrencyEnum currency;
  private final Set<TagDto> tags;

  public TransactionDto(Builder builder) {
    this.transactionId = builder.transactionId;
    this.username = builder.username;
    this.accountId = builder.accountId;
    this.budgetId = builder.budgetId;
    this.date = builder.date;
    this.description = builder.description;
    this.amount = builder.amount;
    this.currency = builder.currency;
    this.tags = builder.tags;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private UUID transactionId;
    private String username;
    private UUID accountId;
    private UUID budgetId;
    private LocalDate date;
    private String description;
    private BigDecimal amount;
    private CurrencyEnum currency;
    private Set<TagDto> tags;

    public Builder withTransactionId(UUID transactionId) {
      this.transactionId = transactionId;
      return this;
    }

    public Builder withUsername(String username) {
      this.username = username;
      return this;
    }

    public Builder withAccountId(UUID accountId) {
      this.accountId = accountId;
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

    public Builder withCurrency(CurrencyEnum currency) {
      this.currency = currency;
      return this;
    }

    public Builder withTags(Set<TagDto> tags) {
      this.tags = tags;
      return this;
    }

    public TransactionDto build() {
      return new TransactionDto(this);
    }
  }

  public UUID getTransactionId() {
    return transactionId;
  }

  public String getUsername() {
    return username;
  }

  public UUID getAccountId() {
    return accountId;
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

  public CurrencyEnum getCurrency() {
    return currency;
  }

  public Set<TagDto> getTags() {
    return tags;
  }
}
