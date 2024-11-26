package it.moneyverse.account.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import it.moneyverse.enums.AccountCategoryEnum;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountDto implements Serializable {

  private UUID accountId;
  private UUID userId;
  private String accountName;
  private BigDecimal balance;
  private BigDecimal balanceTarget;
  private AccountCategoryEnum accountCategory;
  private String accountDescription;
  private Boolean isDefault;

  public AccountDto() {}

  public AccountDto(Builder builder) {
    this.accountId = builder.accountId;
    this.userId = builder.userId;
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
    private UUID userId;
    private String accountName;
    private BigDecimal balance;
    private BigDecimal balanceTarget;
    private AccountCategoryEnum accountCategory;
    private String accountDescription;
    private Boolean isDefault;

    public Builder accountId(UUID accountId) {
      this.accountId = accountId;
      return this;
    }

    public Builder userId(UUID userId) {
      this.userId = userId;
      return this;
    }

    public Builder accountName(String accountName) {
      this.accountName = accountName;
      return this;
    }

    public Builder balance(BigDecimal balance) {
      this.balance = balance;
      return this;
    }

    public Builder balanceTarget(BigDecimal balanceTarget) {
      this.balanceTarget = balanceTarget;
      return this;
    }

    public Builder accountCategory(AccountCategoryEnum accountCategory) {
      this.accountCategory = accountCategory;
      return this;
    }

    public Builder accountDescription(String accountDescription) {
      this.accountDescription = accountDescription;
      return this;
    }

    public Builder isDefault(Boolean isDefault) {
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
}
