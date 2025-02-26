package it.moneyverse.test.utils;

import it.moneyverse.core.model.dto.BoundCriteria;
import it.moneyverse.core.model.dto.DateCriteria;
import it.moneyverse.core.model.events.TransactionEvent;
import java.time.LocalDate;
import java.util.UUID;

public class FakeUtils {

  public static final String FAKE_USER = "FAKE_USER";
  public static final String ONBOARD = "ONBOARD";
  public static final Integer MIN_USERS = 10;
  public static final Integer MAX_USERS = 50;
  public static final Integer MIN_ACCOUNTS_PER_USER = 5;
  public static final Integer MAX_ACCOUNTS_PER_USER = 20;
  public static final Integer ACCOUNT_CATEGORY_NUMBER = 10;
  public static final Integer MIN_CATEGORIES_PER_USER = 3;
  public static final Integer MAX_CATEGORIES_PER_USER = 15;
  public static final Integer DEFAULT_BUDGETS_PER_USER = 3;
  public static final Integer MIN_TRANSACTION_PER_USER = 25;
  public static final Integer MAX_TRANSACTION_PER_USER = 100;
  public static final Integer MIN_TAGS_PER_USER = 0;
  public static final Integer MAX_TAGS_PER_USER = 10;
  public static final Integer MIN_PREFERENCES = 3;
  public static final Integer MAX_PREFERENCES = 10;
  public static final Integer MIN_BUDGETS_PER_CATEGORY = 0;
  public static final Integer MAX_BUDGETS_PER_CATEGORY = 3;
  public static final Integer MIN_SUBSCRIPTIONS_PER_USER = 0;
  public static final Integer MAX_SUBSCRIPTIONS_PER_USER = 3;

  public static DateCriteria randomDateCriteria() {
    DateCriteria date = new DateCriteria();
    LocalDate lower = RandomUtils.randomLocalDate(2024, 2025);
    date.setStart(lower);
    date.setEnd(lower.plusMonths(3));
    return date;
  }

  public static BoundCriteria randomBoundCriteria() {
    BoundCriteria bound = new BoundCriteria();
    bound.setUpper(RandomUtils.randomBigDecimal());
    bound.setLower(RandomUtils.randomBigDecimal());
    return bound;
  }

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
