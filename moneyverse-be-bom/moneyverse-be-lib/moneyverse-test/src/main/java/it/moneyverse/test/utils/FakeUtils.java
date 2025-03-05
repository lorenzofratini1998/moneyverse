package it.moneyverse.test.utils;

import it.moneyverse.core.model.events.TransactionEvent;
import java.util.UUID;

public class FakeUtils {

  public static TransactionEvent randomTransactionEvent(UUID budgetId) {
    return TransactionEvent.builder()
        .withTransactionId(RandomUtils.randomUUID())
        .withAccountId(RandomUtils.randomUUID())
        .withCategoryId(RandomUtils.randomUUID())
        .withBudgetId(budgetId)
        .withDate(RandomUtils.randomLocalDate(2025, 2025))
        .withAmount(RandomUtils.randomBigDecimal())
        // .withPreviousAmount(RandomUtils.randomBigDecimal())
        .build();
  }

  private FakeUtils() {}
}
