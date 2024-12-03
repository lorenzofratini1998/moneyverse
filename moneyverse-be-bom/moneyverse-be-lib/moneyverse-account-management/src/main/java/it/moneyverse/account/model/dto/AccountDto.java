package it.moneyverse.account.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import it.moneyverse.core.enums.AccountCategoryEnum;
import it.moneyverse.core.utils.JsonUtils;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = AccountDto.Builder.class)
public class AccountDto implements Serializable {

  private final UUID accountId;
  private final String username;
  private final String accountName;
  private final BigDecimal balance;
  private final BigDecimal balanceTarget;
  private final AccountCategoryEnum accountCategory;
  private final String accountDescription;
  private final Boolean isDefault;

  public AccountDto(Builder builder) {
    this.accountId = builder.accountId;
    this.username = builder.username;
    this.accountName = builder.accountName;
    this.balance = builder.balance;
    this.balanceTarget = builder.balanceTarget;
    this.accountCategory = builder.accountCategory;
    this.accountDescription = builder.accountDescription;
    this.isDefault = builder.isDefault;
  }

  public UUID getAccountId() {
    return accountId;
  }

  public String getUsername() {
    return username;
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

  public AccountCategoryEnum getAccountCategory() {
    return accountCategory;
  }

  public String getAccountDescription() {
    return accountDescription;
  }

  public Boolean isDefault() {
    return isDefault;
  }

  public static class Builder {

    private UUID accountId;
    private String username;
    private String accountName;
    private BigDecimal balance;
    private BigDecimal balanceTarget;
    private AccountCategoryEnum accountCategory;
    private String accountDescription;
    private Boolean isDefault;

    public Builder withAccountId(UUID accountId) {
      this.accountId = accountId;
      return this;
    }

    public Builder withUsername(String username) {
      this.username = username;
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

    public Builder withAccountCategory(AccountCategoryEnum accountCategory) {
      this.accountCategory = accountCategory;
      return this;
    }

    public Builder withAccountDescription(String accountDescription) {
      this.accountDescription = accountDescription;
      return this;
    }

    public Builder withDefault(Boolean isDefault) {
      this.isDefault = isDefault;
      return this;
    }

    public AccountDto build() {
      return new AccountDto(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }
}
