package it.moneyverse.analytics.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = FrequencyUseAccountDto.Builder.class)
public class FrequencyUseAccountDto {
  private final UUID accountId;
  private final Integer numberOfTransactions;

  public static class Builder {
    private UUID accountId;
    private Integer numberOfTransactions;

    public Builder accountId(UUID accountId) {
      this.accountId = accountId;
      return this;
    }

    public Builder numberOfTransactions(Integer numberOfTransactions) {
      this.numberOfTransactions = numberOfTransactions;
      return this;
    }

    public FrequencyUseAccountDto build() {
      return new FrequencyUseAccountDto(this);
    }
  }

  public FrequencyUseAccountDto(Builder builder) {
    this.accountId = builder.accountId;
    this.numberOfTransactions = builder.numberOfTransactions;
  }

  public static Builder builder() {
    return new Builder();
  }

  public UUID getAccountId() {
    return accountId;
  }

  public Integer getNumberOfTransactions() {
    return numberOfTransactions;
  }
}
