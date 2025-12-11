package it.moneyverse.core.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import it.moneyverse.core.utils.JsonUtils;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = AccountDto.Builder.class)
public class AccountDto implements Serializable {

  private final UUID accountId;
  private final UUID userId;
  private final String accountName;
  private final BigDecimal balance;
  private final BigDecimal balanceTarget;
  private final String accountCategory;
  private final String accountDescription;
  private final Boolean isDefault;
  private final String currency;
  private final StyleDto style;

  public AccountDto(Builder builder) {
    this.accountId = builder.accountId;
    this.userId = builder.userId;
    this.accountName = builder.accountName;
    this.balance = builder.balance;
    this.balanceTarget = builder.balanceTarget;
    this.accountCategory = builder.accountCategory;
    this.accountDescription = builder.accountDescription;
    this.currency = builder.currency;
    this.isDefault = builder.isDefault;
    this.style = builder.style;
  }

  public static Builder builder() {
    return new Builder();
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

  public String getAccountCategory() {
    return accountCategory;
  }

  public String getAccountDescription() {
    return accountDescription;
  }

  public String getCurrency() {
    return currency;
  }

  public Boolean isDefault() {
    return isDefault;
  }

  public StyleDto getStyle() {
    return style;
  }

  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }

  public static class Builder {

    private UUID accountId;
    private UUID userId;
    private String accountName;
    private BigDecimal balance;
    private BigDecimal balanceTarget;
    private String accountCategory;
    private String accountDescription;
    private Boolean isDefault;
    private String currency;
    private StyleDto style;

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

    public Builder withAccountCategory(String accountCategory) {
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

    public Builder withCurrency(String currency) {
      this.currency = currency;
      return this;
    }

    public Builder withStyle(StyleDto style) {
      this.style = style;
      return this;
    }

    public AccountDto build() {
      return new AccountDto(this);
    }
  }
}
