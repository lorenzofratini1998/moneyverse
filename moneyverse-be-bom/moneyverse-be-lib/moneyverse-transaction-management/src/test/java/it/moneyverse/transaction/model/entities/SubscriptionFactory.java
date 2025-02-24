package it.moneyverse.transaction.model.entities;

import static it.moneyverse.test.utils.FakeUtils.*;

import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.test.utils.RandomUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SubscriptionFactory {

  public static List<Subscription> createSubscriptions(
      List<UserModel> users, List<Transaction> transactions) {
    List<Subscription> subscriptions = new ArrayList<>();
    for (UserModel user : users) {
      for (int i = 0;
          i < RandomUtils.randomInteger(MIN_SUBSCRIPTIONS_PER_USER, MAX_SUBSCRIPTIONS_PER_USER);
          i++) {
        Subscription subscription = createSubscription(user, transactions);
        subscriptions.add(subscription);
      }
    }
    transactions.addAll(
        subscriptions.stream().map(Subscription::getTransactions).flatMap(List::stream).toList());
    return subscriptions;
  }

  private static Subscription createSubscription(UserModel user, List<Transaction> transactions) {
    final List<UUID> userAccounts =
        transactions.stream()
            .filter(t -> t.getUserId().equals(user.getUserId()))
            .map(Transaction::getAccountId)
            .toList();
    final List<UUID> userCategories =
        transactions.stream()
            .filter(t -> t.getUserId().equals(user.getUserId()))
            .map(Transaction::getCategoryId)
            .toList();
    final int duration = RandomUtils.randomInteger(1, 12);
    final Integer alreadyExecuted = RandomUtils.randomInteger(0, duration - 1);
    Subscription subscription = new Subscription();
    subscription.setSubscriptionId(RandomUtils.randomUUID());
    subscription.setUserId(user.getUserId());
    subscription.setAccountId(
        userAccounts.get(RandomUtils.randomInteger(0, userAccounts.size() - 1)));
    subscription.setCategoryId(
        userCategories.get(RandomUtils.randomInteger(0, userCategories.size() - 1)));
    subscription.setAmount(RandomUtils.randomBigDecimal().multiply(BigDecimal.valueOf(duration)));
    subscription.setCurrency(RandomUtils.randomString(3).toUpperCase());
    subscription.setSubscriptionName(RandomUtils.randomString(30));
    subscription.setRecurrenceRule("FREQ=MONTHLY");
    subscription.setStartDate(LocalDate.now().minusMonths(alreadyExecuted));
    subscription.setNextExecutionDate(subscription.getStartDate().plusMonths(alreadyExecuted + 1));
    subscription.setEndDate(
        Math.random() < 0.5 ? subscription.getStartDate().plusMonths(duration) : null);
    subscription.setTransactions(
        createSubscriptionTransactions(alreadyExecuted, duration, subscription));
    subscription.setCreatedBy(FAKE_USER);
    subscription.setCreatedAt(LocalDateTime.now());
    subscription.setUpdatedBy(FAKE_USER);
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
      transaction.setCreatedBy(FAKE_USER);
      transaction.setCreatedAt(LocalDateTime.now());
      transaction.setUpdatedBy(FAKE_USER);
      transaction.setUpdatedAt(LocalDateTime.now());
      transactions.add(transaction);
    }
    return transactions;
  }
}
