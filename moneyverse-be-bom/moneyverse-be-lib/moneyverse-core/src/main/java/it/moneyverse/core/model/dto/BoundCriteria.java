package it.moneyverse.core.model.dto;

import java.math.BigDecimal;
import java.util.Optional;

public class BoundCriteria {

  private BigDecimal lower;
  private BigDecimal upper;

  public Optional<BigDecimal> getLower() {
    return Optional.ofNullable(lower);
  }

  public void setLower(BigDecimal lower) {
    this.lower = lower;
  }

  public Optional<BigDecimal> getUpper() {
    return Optional.ofNullable(upper);
  }

  public void setUpper(BigDecimal upper) {
    this.upper = upper;
  }
}
