package it.moneyverse.analytics.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.Serializable;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = AccountAnalyticsKpiDto.Builder.class)
public class AccountAnalyticsKpiDto implements Serializable {
  private final PeriodDto period;
  private final AmountDto totalAmount;
  private final CountDto numberOfActiveAccounts;
  private final UUID mostUsedAccount;
  private final UUID leastUsedAccount;
  private final AccountAnalyticsKpiDto compare;

  public static class Builder {
    private PeriodDto period;
    private AmountDto totalAmount;
    private CountDto numberOfActiveAccounts;
    private UUID mostUsedAccount;
    private UUID leastUsedAccount;
    private AccountAnalyticsKpiDto compare;

    public Builder withPeriod(PeriodDto period) {
      this.period = period;
      return this;
    }

    public Builder withTotalAmount(AmountDto totalAmount) {
      this.totalAmount = totalAmount;
      return this;
    }

    public Builder withNumberOfActiveAccounts(CountDto numberOfActiveAccounts) {
      this.numberOfActiveAccounts = numberOfActiveAccounts;
      return this;
    }

    public Builder withMostUsedAccount(UUID mostUsedAccount) {
      this.mostUsedAccount = mostUsedAccount;
      return this;
    }

    public Builder withLeastUsedAccount(UUID leastUsedAccount) {
      this.leastUsedAccount = leastUsedAccount;
      return this;
    }

    public Builder withCompare(AccountAnalyticsKpiDto compare) {
      this.compare = compare;
      return this;
    }

    public AccountAnalyticsKpiDto build() {
      return new AccountAnalyticsKpiDto(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  public AccountAnalyticsKpiDto(Builder builder) {
    this.period = builder.period;
    this.totalAmount = builder.totalAmount;
    this.numberOfActiveAccounts = builder.numberOfActiveAccounts;
    this.mostUsedAccount = builder.mostUsedAccount;
    this.leastUsedAccount = builder.leastUsedAccount;
    this.compare = builder.compare;
  }

  public PeriodDto getPeriod() {
    return period;
  }

  public AmountDto getTotalAmount() {
    return totalAmount;
  }

  public CountDto getNumberOfActiveAccounts() {
    return numberOfActiveAccounts;
  }

  public UUID getMostUsedAccount() {
    return mostUsedAccount;
  }

  public UUID getLeastUsedAccount() {
    return leastUsedAccount;
  }

  public AccountAnalyticsKpiDto getCompare() {
    return compare;
  }
}
