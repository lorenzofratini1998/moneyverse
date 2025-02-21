package it.moneyverse.transaction.utils;

import it.moneyverse.transaction.model.entities.Subscription;
import it.moneyverse.transaction.model.entities.Transaction;
import java.time.LocalDate;
import java.util.List;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.property.RRule;

public class SubscriptionUtils {

  public static final String BATCH_JOB = "batchJob";

  public static LocalDate calculateNextExecutionDate(Subscription subscription) {
    LocalDate today = LocalDate.now();
    if (subscription.getStartDate().isAfter(today)) {
      return subscription.getStartDate();
    }
    RRule<LocalDate> rrule = new RRule<>(subscription.getRecurrenceRule());
    Recur<LocalDate> recur = rrule.getRecur();
    LocalDate startInterval = subscription.getStartDate();
    LocalDate endInterval = today.plusYears(3);
    if (subscription.getEndDate() != null) {
      endInterval =
          subscription.getEndDate().isBefore(endInterval) ? subscription.getEndDate() : endInterval;
    }
    List<LocalDate> dates =
        recur.getDates(startInterval, endInterval).stream()
            .filter(date -> date.isAfter(today))
            .toList();
    return dates.isEmpty() ? null : dates.getFirst();
  }

  public static Transaction createSubscriptionTransaction(
      Subscription subscription, LocalDate date) {
    Transaction transaction = new Transaction();
    transaction.setUserId(subscription.getUserId());
    transaction.setAccountId(subscription.getAccountId());
    transaction.setCategoryId(subscription.getCategoryId());
    transaction.setAmount(subscription.getAmount());
    transaction.setCurrency(subscription.getCurrency());
    transaction.setDescription(subscription.getSubscriptionName());
    transaction.setDate(date);
    return transaction;
  }

  private SubscriptionUtils() {}
}
