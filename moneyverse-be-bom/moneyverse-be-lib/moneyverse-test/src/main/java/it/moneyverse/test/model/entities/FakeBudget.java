package it.moneyverse.test.model.entities;

import it.moneyverse.core.model.entities.BudgetModel;
import it.moneyverse.test.utils.RandomUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class FakeBudget extends FakeAuditable implements BudgetModel {

  private final UUID budgetId;
  private final String username;
  private final String budgetName;
  private final String description;
  private final BigDecimal budgetLimit;
  private final BigDecimal amount;

  public FakeBudget(String username, Integer counter) {
    counter++;
    this.budgetId = RandomUtils.randomUUID();
    this.username = username;
    this.budgetName = "Budget %s".formatted(counter);
    this.description = RandomUtils.randomString(30);
    this.budgetLimit =
        (int) (Math.random() * 100) % 2 == 0
            ? RandomUtils.randomDecimal(0.0, Math.random() * 2000)
                .setScale(2, RoundingMode.HALF_EVEN)
            : null;
    this.amount = RandomUtils.randomBigDecimal().setScale(2, RoundingMode.HALF_EVEN);
  }

  @Override
  public UUID getBudgetId() {
    return budgetId;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public String getBudgetName() {
    return budgetName;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public BigDecimal getBudgetLimit() {
    return budgetLimit;
  }

  @Override
  public BigDecimal getAmount() {
    return amount;
  }
}
