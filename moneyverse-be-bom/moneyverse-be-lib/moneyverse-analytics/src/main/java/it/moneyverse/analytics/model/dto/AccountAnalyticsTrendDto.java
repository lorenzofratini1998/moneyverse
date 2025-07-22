package it.moneyverse.analytics.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = AccountAnalyticsTrendDto.Builder.class)
public class AccountAnalyticsTrendDto implements Serializable {
  private final PeriodDto period;
  private final UUID accountId;
  private final List<AmountDto> data;
  private final AccountAnalyticsTrendDto compare;

  public static class Builder {
    private PeriodDto period;
    private UUID accountId;
    private List<AmountDto> data;
    private AccountAnalyticsTrendDto compare;

    public Builder withPeriod(PeriodDto period) {
      this.period = period;
      return this;
    }

    public Builder withAccountId(UUID accountId) {
      this.accountId = accountId;
      return this;
    }

    public Builder withData(List<AmountDto> data) {
      this.data = data;
      return this;
    }

    public Builder withCompare(AccountAnalyticsTrendDto compare) {
      this.compare = compare;
      return this;
    }

    public AccountAnalyticsTrendDto build() {
      return new AccountAnalyticsTrendDto(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  public AccountAnalyticsTrendDto(Builder builder) {
    this.period = builder.period;
    this.accountId = builder.accountId;
    this.data = builder.data;
    this.compare = builder.compare;
  }

  public PeriodDto getPeriod() {
    return period;
  }

  public UUID getAccountId() {
    return accountId;
  }

  public List<AmountDto> getData() {
    return data;
  }

  public AccountAnalyticsTrendDto getCompare() {
    return compare;
  }
}
