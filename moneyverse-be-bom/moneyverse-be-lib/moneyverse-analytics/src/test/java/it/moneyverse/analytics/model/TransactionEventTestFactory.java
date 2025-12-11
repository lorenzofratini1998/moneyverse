package it.moneyverse.analytics.model;

import it.moneyverse.analytics.model.entities.TransactionEvent;
import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.test.model.TestFactory;
import it.moneyverse.test.utils.RandomUtils;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionEventTestFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(TransactionEventTestFactory.class);

  public static List<TransactionEvent> createTransactionEvents(List<UserModel> users) {
    List<TransactionEvent> events =
        users.stream()
            .map(user -> fakeUserTransactionEvent(user.getUserId()))
            .flatMap(List::stream)
            .collect(Collectors.toList());
    LOGGER.info("Created {} random transaction events for testing", events.size());
    return events;
  }

  private static List<TransactionEvent> fakeUserTransactionEvent(UUID userId) {
    List<UUID> accounts =
        randomUUIDs(TestFactory.MIN_ACCOUNTS_PER_USER, TestFactory.MAX_ACCOUNTS_PER_USER);
    List<UUID> categories =
        randomUUIDs(TestFactory.MIN_CATEGORIES_PER_USER, TestFactory.MAX_CATEGORIES_PER_USER);
    List<UUID> budgets =
        randomUUIDs(TestFactory.MIN_BUDGETS_PER_CATEGORY, TestFactory.MAX_BUDGETS_PER_CATEGORY);
    List<UUID> tags = randomUUIDs(TestFactory.MIN_TAGS_PER_USER, TestFactory.MAX_TAGS_PER_USER);

    int transactionsPerUser =
        RandomUtils.randomInteger(
            TestFactory.MIN_TRANSACTION_PER_USER, TestFactory.MAX_TRANSACTION_PER_USER);

    List<TransactionEvent> events = new ArrayList<>();
    List<TransactionEvent> createEvents = new ArrayList<>();

    for (int i = 0; i < transactionsPerUser; i++) {
      TransactionEvent event = fakeTransactionEvent(userId, accounts, categories, budgets, tags);
      event.setEventType(0);
      event.setOriginalTransactionId(null);
      createEvents.add(event);
      events.add(event);
    }

    // RANDOM UPDATE and DELETE events
    for (TransactionEvent createEvent : createEvents) {
      UUID transactionId = createEvent.getTransactionId();
      LocalDateTime baseTimestamp = createEvent.getEventTimestamp();

      int updates = RandomUtils.randomInteger(0, 3);
      LocalDateTime lastTimestamp = baseTimestamp;
      for (int i = 0; i < updates; i++) {
        lastTimestamp = lastTimestamp.plusMinutes(RandomUtils.randomInteger(1, 120));
        TransactionEvent updateEvent =
            fakeTransactionEvent(userId, accounts, categories, budgets, tags);
        updateEvent.setEventType(1);
        updateEvent.setOriginalTransactionId(transactionId);
        updateEvent.setDate(lastTimestamp.toLocalDate());
        updateEvent.setEventTimestamp(lastTimestamp);
        events.add(updateEvent);
      }

      if (RandomUtils.flipCoin()) {
        lastTimestamp = lastTimestamp.plusMinutes(RandomUtils.randomInteger(1, 120));
        TransactionEvent deleteEvent =
            fakeTransactionEvent(userId, accounts, categories, budgets, tags);
        deleteEvent.setEventType(2);
        deleteEvent.setOriginalTransactionId(transactionId);
        deleteEvent.setDate(lastTimestamp.toLocalDate());
        deleteEvent.setEventTimestamp(lastTimestamp);
        events.add(deleteEvent);
      }
    }
    return events;
  }

  private static TransactionEvent fakeTransactionEvent(
      UUID userId,
      List<UUID> accounts,
      List<UUID> categories,
      List<UUID> budgets,
      List<UUID> tags) {
    TransactionEvent event = new TransactionEvent();
    UUID transactionId = RandomUtils.randomUUID();
    event.setEventId(RandomUtils.randomUUID());
    event.setTransactionId(transactionId);
    event.setUserId(userId);
    event.setAccountId(accounts.get(RandomUtils.randomInteger(accounts.size())));
    event.setCategoryId(
        RandomUtils.flipCoin()
            ? categories.get(RandomUtils.randomInteger(categories.size()))
            : null);
    event.setTags(
        !tags.isEmpty() && RandomUtils.flipCoin() ? RandomUtils.randomSubList(tags) : null);
    event.setBudgetId(
        !budgets.isEmpty() && RandomUtils.flipCoin()
            ? budgets.get(RandomUtils.randomInteger(budgets.size()))
            : null);
    event.setAmount(randomAmount());
    event.setNormalizedAmount(event.getAmount());
    event.setCurrency("EUR");
    event.setDate(RandomUtils.randomLocalDate(2024, 2025));
    event.setEventTimestamp(LocalDateTime.of(event.getDate(), RandomUtils.randomLocalTime()));
    return event;
  }

  private static List<UUID> randomUUIDs(int min, int max) {
    return IntStream.range(0, RandomUtils.randomInteger(min, max))
        .mapToObj(i -> RandomUtils.randomUUID())
        .collect(Collectors.toList());
  }

  private static BigDecimal randomAmount() {
    BigDecimal base = RandomUtils.randomBigDecimal();
    if (RandomUtils.flipCoin()) {
      return base.add(BigDecimal.ONE).multiply(BigDecimal.valueOf(499)).negate();
    } else {
      return base.add(BigDecimal.ONE).multiply(BigDecimal.valueOf(1999));
    }
  }
}
