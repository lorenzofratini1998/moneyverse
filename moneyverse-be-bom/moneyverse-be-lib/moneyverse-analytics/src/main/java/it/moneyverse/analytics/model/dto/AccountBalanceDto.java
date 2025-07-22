package it.moneyverse.analytics.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.Serializable;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = AccountBalanceDto.Builder.class)
public class AccountBalanceDto implements Serializable {
  private final UUID accountId;
  private final AmountDto amount;

  public static class Builder {
    private UUID accountId;
    private AmountDto amount;

    public Builder withAccountId(UUID accountId) {
      this.accountId = accountId;
      return this;
    }

    public Builder withAmount(AmountDto amount) {
      this.amount = amount;
      return this;
    }

    public AccountBalanceDto build() {
      return new AccountBalanceDto(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  public AccountBalanceDto(Builder builder) {
    this.accountId = builder.accountId;
    this.amount = builder.amount;
  }

  public UUID getAccountId() {
    return accountId;
  }

  public AmountDto getAmount() {
    return amount;
  }
}
