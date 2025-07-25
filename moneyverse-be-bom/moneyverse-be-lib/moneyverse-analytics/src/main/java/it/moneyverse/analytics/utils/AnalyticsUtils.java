package it.moneyverse.analytics.utils;

import it.moneyverse.analytics.model.dto.AmountDto;
import it.moneyverse.analytics.model.dto.CountDto;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Function;

public class AnalyticsUtils {

  public static BigDecimal calculateTrend(BigDecimal current, BigDecimal compare) {
    if (compare == null || compare.equals(BigDecimal.ZERO)) {
      return null;
    }
    return current.subtract(compare).divide(compare.abs(), 4, RoundingMode.HALF_UP);
  }

  public static <T> CountDto getCount(T current, T compare, Function<T, Integer> extractor) {
    Integer currentCount = extractor.apply(current);
    Integer compareCount = compare != null ? extractor.apply(compare) : null;
    Integer variation = null;

    if (compareCount != null && compareCount != 0) {
      variation = currentCount - compareCount;
    }

    return CountDto.builder().withCount(currentCount).withVariation(variation).build();
  }

  public static <T> CountDto getCount(T current, Function<T, Integer> extractor) {
    return getCount(current, null, extractor);
  }

  public static <T> AmountDto getAmount(
      T current, T compare, Function<T, BigDecimal> amountExtractor) {
    BigDecimal currentAmount = amountExtractor.apply(current);
    BigDecimal compareAmount = compare != null ? amountExtractor.apply(compare) : null;

    BigDecimal variation = null;
    if (compareAmount != null && compareAmount.compareTo(BigDecimal.ZERO) != 0) {
      variation = AnalyticsUtils.calculateTrend(currentAmount, compareAmount);
    }

    return AmountDto.builder().withAmount(currentAmount).withVariation(variation).build();
  }

  public static <T> AmountDto getAmount(T current, Function<T, BigDecimal> amountExtractor) {
    return getAmount(current, null, amountExtractor);
  }

  private AnalyticsUtils() {}
}
