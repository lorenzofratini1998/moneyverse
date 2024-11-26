package it.moneyverse.test.model;

import it.moneyverse.enums.AccountCategoryEnum;
import it.moneyverse.model.entities.AccountModel;
import it.moneyverse.test.utils.RandomUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class FakeAccount extends FakeAuditable implements AccountModel {

  private final Integer counter;
  private final UUID userId;

  public FakeAccount(UUID userId, Integer counter) {
    this.counter = counter + 1;
    this.userId = userId;
  }

  @Override
  public UUID getAccountId() {
    return RandomUtils.randomUUID();
  }

  @Override
  public UUID getUserId() {
    return userId;
  }

  @Override
  public String getAccountName() {
    return "Account %s".formatted(counter);
  }

  @Override
  public BigDecimal getBalance() {
    return RandomUtils.randomDecimal(0.0, Math.random() * 1000).setScale(2, RoundingMode.HALF_EVEN);
  }

  @Override
  public BigDecimal getBalanceTarget() {
    return (int) (Math.random() * 100) % 2 == 0 ? RandomUtils.randomDecimal(0.0,
        Math.random() * 2000).setScale(2, RoundingMode.HALF_EVEN) : null;
  }

  @Override
  public AccountCategoryEnum getAccountCategory() {
    return RandomUtils.randomEnum(AccountCategoryEnum.class);
  }

  @Override
  public String getAccountDescription() {
    return "Account Description %s".formatted(counter);
  }

  @Override
  public Boolean isDefault() {
    return counter == 0;
  }
}
