package it.moneyverse.core.model.dto;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.format.annotation.DateTimeFormat;

public class DateCriteria {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate start;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate end;

  public boolean matches(LocalDate date) {
    if (date == null) {
      return false;
    }
    if (start != null && date.isBefore(start)) {
      return false;
    }
    return end == null || !date.isAfter(end);
  }

  public Optional<LocalDate> getStart() {
    return Optional.ofNullable(start);
  }

  public void setStart(LocalDate start) {
    this.start = start;
  }

  public Optional<LocalDate> getEnd() {
    return Optional.ofNullable(end);
  }

  public void setEnd(LocalDate end) {
    this.end = end;
  }
}
