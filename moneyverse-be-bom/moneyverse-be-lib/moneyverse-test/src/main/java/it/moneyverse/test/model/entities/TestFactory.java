package it.moneyverse.test.model.entities;

import it.moneyverse.core.model.dto.BoundCriteria;
import it.moneyverse.core.model.dto.DateCriteria;
import it.moneyverse.core.model.events.TransactionEvent;
import it.moneyverse.core.model.events.UserDeletionEvent;
import it.moneyverse.test.utils.RandomUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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

  public static UserDeletionEvent fakeUserDeletionEvent(UUID userId) {
    return new UserDeletionEvent(userId);
  }

  public static TransactionEvent fakeTransactionEvent(UUID accountId, UUID previousAccountId) {
    return fakeTransactionEventBuilder()
        .withPreviousTransaction(fakeTransactionEvent(previousAccountId))
        .withAccountId(accountId)
        .build();
  }

  public static TransactionEvent fakeTransactionEvent(UUID accountId) {
    return fakeTransactionEventBuilder().withAccountId(accountId).build();
  }

  private static TransactionEvent.Builder fakeTransactionEventBuilder() {
    return TransactionEvent.builder()
        .withTransactionId(RandomUtils.randomUUID())
        .withAmount(RandomUtils.randomBigDecimal())
        .withCurrency(RandomUtils.randomCurrency())
        .withDate(RandomUtils.randomDate());
  }

  public static DateCriteria fakeDateCriteria() {
    DateCriteria date = new DateCriteria();
    LocalDate lower = RandomUtils.randomDate();
    date.setStart(lower);
    date.setEnd(lower.plusMonths(RandomUtils.randomInteger(1, 12)));
    return date;
  }

  public static BoundCriteria randomBoundCriteria() {
    BoundCriteria bound = new BoundCriteria();
    BigDecimal lower = RandomUtils.randomBigDecimal();
    bound.setLower(lower);
    bound.setUpper(lower.add(RandomUtils.randomBigDecimal()));
    return bound;
  }

  public static BoundCriteria randomBoundCriteria(List<BigDecimal> values) {
    BigDecimal minBalance = findMin(values);
    BigDecimal maxBalance = findMax(values);
    BoundCriteria criteria = new BoundCriteria();
    criteria.setLower(
        RandomUtils.randomDecimal(
                minBalance.doubleValue(),
                maxBalance.divide(BigDecimal.TWO, RoundingMode.HALF_DOWN).doubleValue())
            .setScale(2, RoundingMode.HALF_DOWN));
    criteria.setUpper(
        RandomUtils.randomDecimal(
                maxBalance.divide(BigDecimal.TWO, RoundingMode.HALF_DOWN).doubleValue(),
                maxBalance.doubleValue())
            .setScale(2, RoundingMode.HALF_DOWN));
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

  private TestFactory() {}
}
