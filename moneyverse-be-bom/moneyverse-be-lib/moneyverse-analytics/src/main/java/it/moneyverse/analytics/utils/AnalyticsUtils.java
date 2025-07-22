package it.moneyverse.analytics.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AnalyticsUtils {

  public static BigDecimal calculateTrend(BigDecimal current, BigDecimal compare) {
    if (compare == null || compare.equals(BigDecimal.ZERO)) {
      return null;
    }
    return current.subtract(compare).divide(compare.abs(), 4, RoundingMode.HALF_UP);
  }

  private AnalyticsUtils() {}
}
