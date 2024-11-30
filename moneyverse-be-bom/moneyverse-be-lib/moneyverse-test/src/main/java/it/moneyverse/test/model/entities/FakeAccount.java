package it.moneyverse.test.model.entities;

import it.moneyverse.core.enums.AccountCategoryEnum;
import it.moneyverse.core.model.entities.AccountModel;
import it.moneyverse.test.utils.RandomUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class FakeAccount extends FakeAuditable implements AccountModel {

  private final UUID accountId;
  private final String username;
  private final String accountName;
  private final BigDecimal balance;
  private final BigDecimal balanceTarget;
  private final AccountCategoryEnum accountCategory;
  private final String accountDescription;
  private final Boolean isDefault;

  public FakeAccount(String username, Integer counter) {
    counter = counter + 1;
    this.username = username;
    this.accountId = RandomUtils.randomUUID();
    this.accountName = "Account %s".formatted(counter);
    this.balance = RandomUtils.randomDecimal(0.0, Math.random() * 1000).setScale(2, RoundingMode.HALF_EVEN);
    this.balanceTarget = (int) (Math.random() * 100) % 2 == 0 ? RandomUtils.randomDecimal(0.0, Math.random() * 2000).setScale(2, RoundingMode.HALF_EVEN) : null;
    this.accountCategory = RandomUtils.randomEnum(AccountCategoryEnum.class);
    this.accountDescription = "Account Description %s".formatted(counter);
    this.isDefault = counter == 1;
  }

  @Override
  public UUID getAccountId() {
    return accountId;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public String getAccountName() {
    return accountName;
  }

  @Override
  public BigDecimal getBalance() {
    return balance;
  }

  @Override
  public BigDecimal getBalanceTarget() {
    return balanceTarget;
  }

  @Override
  public AccountCategoryEnum getAccountCategory() {
    return accountCategory;
  }

  @Override
  public String getAccountDescription() {
    return accountDescription;
  }

  @Override
  public Boolean isDefault() {
    return isDefault;
  }
}
