package it.moneyverse.transaction.model;

import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.test.model.TestFactory;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.transaction.model.dto.RecurrenceDto;
import it.moneyverse.transaction.model.dto.SubscriptionRequestDto;
import it.moneyverse.transaction.model.dto.SubscriptionUpdateRequestDto;
import it.moneyverse.transaction.model.entities.Subscription;
import it.moneyverse.transaction.model.entities.Transaction;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SubscriptionTestFactory {

  private static final Supplier<UUID> FAKE_USER_ID_SUPPLIER = RandomUtils::randomUUID;
  private static final Supplier<UUID> FAKE_ACCOUNT_ID_SUPPLIER = RandomUtils::randomUUID;
  private static final Supplier<UUID> FAKE_CATEGORY_ID_SUPPLIER = RandomUtils::randomUUID;
  private static final Supplier<String> FAKE_SUBSCRIPTION_NAME_SUPPLIER =
      () -> RandomUtils.randomString(10);
  private static final Supplier<BigDecimal> FAKE_AMOUNT_SUPPLIER = RandomUtils::randomBigDecimal;
  private static final Supplier<String> FAKE_CURRENCY_SUPPLIER = RandomUtils::randomCurrency;
  private static final Supplier<LocalDate> FAKE_DATE_SUPPLIER = RandomUtils::randomDate;

  public static List<Subscription> createSubscriptions(
      List<UserModel> users, List<Transaction> transactions) {
    List<Subscription> subscriptions =
        users.stream()
            .map(
                user -> {
                  List<UUID> userAccounts = getUserAccounts(user, transactions);
                  List<UUID> userCategories = getUserCategories(user, transactions);
                  return createUserSubscriptions(user, userAccounts, userCategories);
                })
            .flatMap(List::stream)
            .toList();
    transactions.addAll(
        subscriptions.stream().map(Subscription::getTransactions).flatMap(List::stream).toList());
    return subscriptions;
  }

  private static List<UUID> getUserAccounts(UserModel user, List<Transaction> transactions) {
    return transactions.stream()
        .filter(t -> t.getUserId().equals(user.getUserId()))
        .map(Transaction::getAccountId)
        .toList();
  }

  private static List<UUID> getUserCategories(UserModel user, List<Transaction> transactions) {
    return transactions.stream()
        .filter(t -> t.getUserId().equals(user.getUserId()))
        .map(Transaction::getCategoryId)
        .toList();
  }

  private static List<Subscription> createUserSubscriptions(
      UserModel user, List<UUID> userAccounts, List<UUID> userCategories) {
    return IntStream.range(
            0,
            RandomUtils.randomInteger(
                TestFactory.MIN_SUBSCRIPTIONS_PER_USER, TestFactory.MAX_SUBSCRIPTIONS_PER_USER))
        .mapToObj(i -> createUserSubscription(user, userAccounts, userCategories))
        .toList();
  }

  private static Subscription createUserSubscription(
      UserModel user, List<UUID> userAccounts, List<UUID> userCategories) {
    final int duration = RandomUtils.randomInteger(1, 12);
    final Integer alreadyExecuted = RandomUtils.randomInteger(duration);
    Subscription subscription = new Subscription();
    subscription.setSubscriptionId(RandomUtils.randomUUID());
    subscription.setUserId(user.getUserId());
    subscription.setAccountId(userAccounts.get(RandomUtils.randomInteger(userAccounts.size())));
    subscription.setCategoryId(
        userCategories.get(RandomUtils.randomInteger(userCategories.size())));
    subscription.setAmount(RandomUtils.randomBigDecimal().multiply(BigDecimal.valueOf(duration)));
    subscription.setCurrency(RandomUtils.randomString(3).toUpperCase());
    subscription.setSubscriptionName(RandomUtils.randomString(30));
    subscription.setRecurrenceRule("FREQ=MONTHLY");
    subscription.setStartDate(LocalDate.now().minusMonths(alreadyExecuted));
    subscription.setNextExecutionDate(subscription.getStartDate().plusMonths(alreadyExecuted + 1));
    subscription.setEndDate(
        RandomUtils.flipCoin() ? subscription.getStartDate().plusMonths(duration) : null);
    subscription.setTransactions(
        createSubscriptionTransactions(alreadyExecuted, duration, subscription));
    subscription.setCreatedBy(TestFactory.FAKE_USER);
    subscription.setCreatedAt(LocalDateTime.now());
    subscription.setUpdatedBy(TestFactory.FAKE_USER);
    subscription.setUpdatedAt(LocalDateTime.now());
    return subscription;
  }

  private static List<Transaction> createSubscriptionTransactions(
      Integer alreadyExecuted, Integer duration, Subscription subscription) {
    List<Transaction> transactions = new ArrayList<>();
    for (int i = 0; i < alreadyExecuted; i++) {
      Transaction transaction = new Transaction();
      transaction.setTransactionId(RandomUtils.randomUUID());
      transaction.setUserId(subscription.getUserId());
      transaction.setAccountId(subscription.getAccountId());
      transaction.setCategoryId(subscription.getCategoryId());
      transaction.setDate(subscription.getStartDate().plusMonths(i + 1));
      transaction.setDescription(subscription.getSubscriptionName());
      transaction.setAmount(
          subscription.getAmount().divide(BigDecimal.valueOf(duration), RoundingMode.HALF_DOWN));
      transaction.setNormalizedAmount(transaction.getAmount());
      transaction.setCurrency(subscription.getCurrency());
      transaction.setCreatedBy(TestFactory.FAKE_USER);
      transaction.setCreatedAt(LocalDateTime.now());
      transaction.setUpdatedBy(TestFactory.FAKE_USER);
      transaction.setUpdatedAt(LocalDateTime.now());
      transactions.add(transaction);
    }
    return transactions;
  }

  public static class SubscriptionRequestBuilder {
    private UUID userId = RandomUtils.randomUUID();
    private UUID accountId = FAKE_ACCOUNT_ID_SUPPLIER.get();
    private UUID categoryId = FAKE_CATEGORY_ID_SUPPLIER.get();
    private final String subscriptionName = FAKE_SUBSCRIPTION_NAME_SUPPLIER.get();
    private final BigDecimal amount = FAKE_AMOUNT_SUPPLIER.get();
    private final String currency = FAKE_CURRENCY_SUPPLIER.get();
    private RecurrenceDto recurrence = fakeRecurrence(FAKE_DATE_SUPPLIER.get());

    public SubscriptionRequestBuilder withUserId(UUID userId) {
      this.userId = userId;
      return this;
    }

    public SubscriptionRequestBuilder withAccountId(UUID accountId) {
      this.accountId = accountId;
      return this;
    }

    public SubscriptionRequestBuilder withCategoryId(UUID categoryId) {
      this.categoryId = categoryId;
      return this;
    }

    public SubscriptionRequestBuilder withRecurrence(LocalDate startDate) {
      this.recurrence = fakeRecurrence(startDate);
      return this;
    }

    public SubscriptionRequestBuilder withRecurrence(LocalDate startDate, LocalDate endDate) {
      this.recurrence = new RecurrenceDto("FREQ=MONTHLY", startDate, endDate);
      return this;
    }

    private SubscriptionRequestBuilder withInvalidRecurrence() {
      this.recurrence = new RecurrenceDto("INVALID", RandomUtils.randomDate(), null);
      return this;
    }

    private SubscriptionRequestBuilder withRecurrenceEndDatePreviousToStartDate() {
      this.recurrence =
          new RecurrenceDto("FREQ=MONTHLY", LocalDate.now(), LocalDate.now().minusMonths(2));
      return this;
    }

    public static Stream<Supplier<SubscriptionRequestDto>> invalidRecurrenceDtoProvider() {
      return Stream.of(
          () -> builder().withInvalidRecurrence().build(),
          () -> builder().withRecurrenceEndDatePreviousToStartDate().build());
    }

    public static SubscriptionRequestDto defaultInstance() {
      return builder().build();
    }

    public static SubscriptionRequestBuilder builder() {
      return new SubscriptionRequestBuilder();
    }

    public SubscriptionRequestDto build() {
      return new SubscriptionRequestDto(
          userId, accountId, categoryId, subscriptionName, amount, currency, recurrence);
    }

    private static RecurrenceDto fakeRecurrence(LocalDate startDate) {
      return new RecurrenceDto("FREQ=MONTHLY", startDate, null);
    }
  }

  public static Subscription fakeSubscription() {
    Subscription subscription = new Subscription();
    subscription.setSubscriptionId(RandomUtils.randomUUID());
    subscription.setUserId(FAKE_USER_ID_SUPPLIER.get());
    subscription.setAccountId(FAKE_ACCOUNT_ID_SUPPLIER.get());
    subscription.setCategoryId(FAKE_CATEGORY_ID_SUPPLIER.get());
    subscription.setSubscriptionName(FAKE_SUBSCRIPTION_NAME_SUPPLIER.get());
    subscription.setAmount(FAKE_AMOUNT_SUPPLIER.get());
    subscription.setCurrency(FAKE_CURRENCY_SUPPLIER.get());
    subscription.setRecurrenceRule("FREQ=MONTHLY");
    subscription.setStartDate(FAKE_DATE_SUPPLIER.get());
    subscription.setEndDate(null);
    return subscription;
  }

  public static SubscriptionUpdateRequestDto fakeSubscriptionUpdateRequest() {
    return new SubscriptionUpdateRequestDto(
        FAKE_ACCOUNT_ID_SUPPLIER.get(),
        FAKE_CATEGORY_ID_SUPPLIER.get(),
        FAKE_SUBSCRIPTION_NAME_SUPPLIER.get(),
        FAKE_AMOUNT_SUPPLIER.get(),
        null,
        null,
        null,
        null,
        null,
        null,
        null);
  }
}
