package it.moneyverse.transaction.utils;

import java.time.LocalDate;
import java.util.List;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.property.RRule;

public class DateCalculator {

  private final Recur<LocalDate> recur;
  private static final LocalDate END_INTERVAL = LocalDate.now().plusYears(2);

  public DateCalculator(String recurrenceRule) {
    RRule<LocalDate> rrule = new RRule<>(recurrenceRule);
    this.recur = rrule.getRecur();
  }

  public LocalDate getNextOccurrence(LocalDate startDate, LocalDate endDate) {
    return getNextOccurrenceInternal(startDate, endDate);
  }

  public LocalDate getNextOccurrence(LocalDate startDate) {
    return getNextOccurrenceInternal(startDate, END_INTERVAL);
  }

  private LocalDate getNextOccurrenceInternal(LocalDate startDate, LocalDate endDate) {
    LocalDate today = LocalDate.now();
    if (startDate.isAfter(today)) {
      return startDate;
    }
    return calculateDates(startDate, endDate).stream()
        .filter(date -> date.isAfter(today))
        .findFirst()
        .orElse(null);
  }

  public List<LocalDate> calculateDates(LocalDate startDate, LocalDate endDate) {
    return recur.getDates(startDate, endDate);
  }
}
