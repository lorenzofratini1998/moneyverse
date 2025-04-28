package it.moneyverse.test.model;

import it.moneyverse.core.enums.EventTypeEnum;
import it.moneyverse.core.model.dto.BoundCriteria;
import it.moneyverse.core.model.dto.DateCriteria;
import it.moneyverse.core.model.dto.StyleRequestDto;
import it.moneyverse.core.model.entities.Style;
import it.moneyverse.core.model.events.AccountEvent;
import it.moneyverse.core.model.events.CategoryEvent;
import it.moneyverse.core.model.events.TransactionEvent;
import it.moneyverse.core.model.events.UserEvent;
import it.moneyverse.test.utils.RandomUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

public class TestFactory {

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

  public static class TransactionEventBuilder {
    private final UUID transactionId = RandomUtils.randomUUID();
    private UUID accountId = RandomUtils.randomUUID();
    private UUID categoryId = RandomUtils.randomUUID();
    private UUID budgetId = RandomUtils.randomUUID();
    private final BigDecimal amount = RandomUtils.randomBigDecimal();
    private final BigDecimal normalizedAmount = RandomUtils.randomBigDecimal();
    private final String currency = RandomUtils.randomCurrency();
    private final LocalDate date = RandomUtils.randomDate();
    private TransactionEvent previousTransaction;
    private final EventTypeEnum eventType = RandomUtils.randomEnum(EventTypeEnum.class);

    public TransactionEventBuilder withAccountId(UUID accountId) {
      this.accountId = accountId;
      return this;
    }

    public TransactionEventBuilder withCategoryId(UUID categoryId) {
      this.categoryId = categoryId;
      return this;
    }

    public TransactionEventBuilder withBudgetId(UUID budgetId) {
      this.budgetId = budgetId;
      return this;
    }

    public TransactionEventBuilder withPreviousTransaction(TransactionEvent previousTransaction) {
      this.previousTransaction = previousTransaction;
      return this;
    }

    public static TransactionEventBuilder builder() {
      return new TransactionEventBuilder();
    }

    public TransactionEvent build() {
      return TransactionEvent.builder()
          .withTransactionId(transactionId)
          .withAccountId(accountId)
          .withCategoryId(categoryId)
          .withBudgetId(budgetId)
          .withAmount(amount)
          .withNormalizedAmount(normalizedAmount)
          .withCurrency(currency)
          .withDate(date)
          .withPreviousTransaction(previousTransaction)
          .withEventType(eventType)
          .build();
    }
  }

  public static UserEvent fakeUserEvent(UUID userId) {
    return UserEvent.builder()
        .withUserId(userId)
        .withEventType(RandomUtils.randomEnum(EventTypeEnum.class))
        .build();
  }

  public static AccountEvent fakeAccountEvent(UUID accountId) {
    return AccountEvent.builder()
        .withAccountId(accountId)
        .withEventType(RandomUtils.randomEnum(EventTypeEnum.class))
        .build();
  }

  public static CategoryEvent fakeCategoryEvent(UUID categoryId) {
    return CategoryEvent.builder()
        .withCategoryId(categoryId)
        .withEventType(RandomUtils.randomEnum(EventTypeEnum.class))
        .build();
  }

  public static DateCriteria fakeDateCriteria() {
    DateCriteria date = new DateCriteria();
    LocalDate lower = RandomUtils.randomDate();
    date.setStart(lower);
    date.setEnd(lower.plusMonths(RandomUtils.randomInteger(1, 12)));
    return date;
  }

  public static BoundCriteria fakeBoundCriteria() {
    BoundCriteria bound = new BoundCriteria();
    BigDecimal lower = RandomUtils.randomBigDecimal();
    bound.setLower(lower);
    bound.setUpper(lower.add(RandomUtils.randomBigDecimal()));
    return bound;
  }

  public static BoundCriteria fakeBoundCriteria(List<BigDecimal> values) {
    BigDecimal minBalance = findMin(values);
    BigDecimal maxBalance = findMax(values);
    if (minBalance.compareTo(maxBalance) == 0) {
      return fakeBoundCriteria();
    }
    double min = minBalance.doubleValue();
    double max = maxBalance.doubleValue();
    BigDecimal lower = RandomUtils.randomDecimal(min, max).setScale(2, RoundingMode.HALF_UP);
    BigDecimal upper =
        RandomUtils.randomDecimal(lower.doubleValue(), max).setScale(2, RoundingMode.HALF_UP);
    BoundCriteria criteria = new BoundCriteria();
    criteria.setLower(lower);
    criteria.setUpper(upper);
    return criteria;
  }

  private static BigDecimal findMin(List<BigDecimal> values) {
    return values.stream()
        .filter(Objects::nonNull)
        .min(Comparator.naturalOrder())
        .orElseThrow(
            () -> new IllegalArgumentException("List is empty or contains only null values"));
  }

  private static BigDecimal findMax(List<BigDecimal> values) {
    return values.stream()
        .filter(Objects::nonNull)
        .max(Comparator.naturalOrder())
        .orElseThrow(
            () -> new IllegalArgumentException("List is empty or contains only null values"));
  }

  public static BigDecimal fakeExchangeRate() {
    return RandomUtils.flipCoin() ? BigDecimal.ONE : RandomUtils.randomBigDecimal();
  }

  public static Style fakeStyle() {
    Style style = new Style();
    style.setBackgroundColor("#FEE2E2");
    style.setTextColor("#EF4444");
    style.setIcon("circle-dollar-sign");
    return style;
  }

  public static StyleRequestDto fakeStyleRequest() {
    return new StyleRequestDto("#FEE2E2", "#EF4444", "circle-dollar-sign");
  }

  private TestFactory() {}
}
