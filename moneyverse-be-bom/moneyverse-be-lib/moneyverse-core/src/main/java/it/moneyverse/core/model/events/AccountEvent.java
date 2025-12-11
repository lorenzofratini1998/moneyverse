package it.moneyverse.core.model.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.math.BigDecimal;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = AccountEvent.Builder.class)
public class AccountEvent extends AbstractEvent {
  private final UUID accountId;
  private final UUID userId;
  private final String accountName;
  private final BigDecimal balance;
  private final BigDecimal balanceTarget;
  private final String currency;
  private final Boolean isDefault;
  private final String accountCategory;

  public AccountEvent(Builder builder) {
    super(builder);
    this.accountId = builder.accountId;
    this.userId = builder.userId;
    this.accountName = builder.accountName;
    this.balance = builder.balance;
    this.balanceTarget = builder.balanceTarget;
    this.currency = builder.currency;
    this.isDefault = builder.isDefault;
    this.accountCategory = builder.accountCategory;
  }

  public static class Builder extends AbstractBuilder<AccountEvent, Builder> {
    private UUID accountId;
    private UUID userId;
    private String accountName;
    private BigDecimal balance;
    private BigDecimal balanceTarget;
    private String currency;
    private Boolean isDefault;
    private String accountCategory;

    public Builder withAccountId(UUID accountId) {
      this.accountId = accountId;
      return this;
    }

    public Builder withUserId(UUID userId) {
      this.userId = userId;
      return this;
    }

    public Builder withAccountName(String accountName) {
      this.accountName = accountName;
      return this;
    }

    public Builder withBalance(BigDecimal balance) {
      this.balance = balance;
      return this;
    }

    public Builder withBalanceTarget(BigDecimal balanceTarget) {
      this.balanceTarget = balanceTarget;
      return this;
    }

    public Builder withCurrency(String currency) {
      this.currency = currency;
      return this;
    }

    public Builder withIsDefault(Boolean isDefault) {
      this.isDefault = isDefault;
      return this;
    }

    public Builder withAccountCategory(String accountCategory) {
      this.accountCategory = accountCategory;
      return this;
    }

    @Override
    protected Builder self() {
      return this;
    }

    @Override
    public AccountEvent build() {
      return new AccountEvent(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public UUID key() {
    return accountId;
  }

  public UUID getAccountId() {
    return accountId;
  }

  public UUID getUserId() {
    return userId;
  }

  public String getAccountName() {
    return accountName;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public BigDecimal getBalanceTarget() {
    return balanceTarget;
  }

  public String getCurrency() {
    return currency;
  }

  public Boolean getDefault() {
    return isDefault;
  }

  public String getAccountCategory() {
    return accountCategory;
  }
}
