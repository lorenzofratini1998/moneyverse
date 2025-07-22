package it.moneyverse.analytics.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.Serializable;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = AccountAnalyticsDistributionDto.Builder.class)
public class AccountAnalyticsDistributionDto implements Serializable {
  private final PeriodDto period;
  private final UUID accountId;
  private final AmountDto totalIncome;
  private final AmountDto totalExpense;
  private final AccountAnalyticsDistributionDto compare;

  public static class Builder {
    private PeriodDto period;
    private UUID accountId;
    private AmountDto totalIncome;
    private AmountDto totalExpense;
    private AccountAnalyticsDistributionDto compare;

    public Builder withPeriod(PeriodDto period) {
      this.period = period;
      return this;
    }

    public Builder withAccountId(UUID accountId) {
      this.accountId = accountId;
      return this;
    }

    public Builder withTotalIncome(AmountDto totalIncome) {
      this.totalIncome = totalIncome;
      return this;
    }

    public Builder withTotalExpense(AmountDto totalExpense) {
      this.totalExpense = totalExpense;
      return this;
    }

    public Builder withCompare(AccountAnalyticsDistributionDto compare) {
      this.compare = compare;
      return this;
    }

    public AccountAnalyticsDistributionDto build() {
      return new AccountAnalyticsDistributionDto(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  public AccountAnalyticsDistributionDto(Builder builder) {
    this.period = builder.period;
    this.accountId = builder.accountId;
    this.totalIncome = builder.totalIncome;
    this.totalExpense = builder.totalExpense;
    this.compare = builder.compare;
  }

  public PeriodDto getPeriod() {
    return period;
  }

  public UUID getAccountId() {
    return accountId;
  }

  public AmountDto getTotalIncome() {
    return totalIncome;
  }

  public AmountDto getTotalExpense() {
    return totalExpense;
  }

  public AccountAnalyticsDistributionDto getCompare() {
    return compare;
  }
}
