package it.moneyverse.transaction.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class DateCalculatorTest {

  private static final String RECURRENCE_RULE = "FREQ=MONTHLY";

  @Test
  void calculateDatesTest() {
    DateCalculator calculator = new DateCalculator(RECURRENCE_RULE);
    LocalDate startDate = LocalDate.of(2025, 1, 1);
    LocalDate endDate = LocalDate.of(2025, 12, 31);

    List<LocalDate> dates = calculator.calculateDates(startDate, endDate);

    assertEquals(12, dates.size());
  }

  @Test
  void getNextOccurrenceTest() {
    DateCalculator calculator = new DateCalculator(RECURRENCE_RULE);
    LocalDate today = LocalDate.now();
    LocalDate endDate = today.plusMonths(12);

    LocalDate nextOccurrence = calculator.getNextOccurrence(today, endDate);

    assertEquals(today.plusMonths(1), nextOccurrence);
  }

  @Test
  void getNextOccurrenceInternalTest() {
    DateCalculator calculator = new DateCalculator(RECURRENCE_RULE);
    LocalDate today = LocalDate.now();

    LocalDate nextOccurrence = calculator.getNextOccurrence(today);

    assertEquals(today.plusMonths(1), nextOccurrence);
  }
}
