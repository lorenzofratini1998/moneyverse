package it.moneyverse.transaction.utils.calculator;

import it.moneyverse.transaction.model.dto.CategoryKPIDto;
import it.moneyverse.transaction.model.dto.PeriodDashboardDto;
import it.moneyverse.transaction.model.dto.projection.CategoryTotalsAndCountsProjection;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class CategoryKpiCalculator {

  public CategoryKPIDto calculate(
      CategoryTotalsAndCountsProjection proj,
      PeriodDashboardDto period,
      Optional<PeriodDashboardDto> comparePeriod) {
    return comparePeriod
        .map(c -> calculateCategoryKPI(proj, period, c))
        .orElseGet(() -> calculateCategoryKPI(proj, period));
  }

  private CategoryKPIDto calculateCategoryKPI(
      CategoryTotalsAndCountsProjection totalsAndCountsProjection, PeriodDashboardDto period) {
    return calculateCategoryKPI(totalsAndCountsProjection, period, null);
  }

  private CategoryKPIDto calculateCategoryKPI(
      CategoryTotalsAndCountsProjection totalsAndCountsProjection,
      PeriodDashboardDto period,
      PeriodDashboardDto comparePeriod) {

    CategoryKPIDto current =
        buildKPI(
            period,
            totalsAndCountsProjection.getCurrentTotal(),
            totalsAndCountsProjection.getCurrentActiveCategoryCount());
    if (comparePeriod != null) {
      CategoryKPIDto previous =
          buildKPI(
              comparePeriod,
              totalsAndCountsProjection.getPreviousTotal(),
              totalsAndCountsProjection.getPreviousActiveCategoryCount());
      return CategoryKPIDto.builder()
          .withPeriod(current.getPeriod())
          .withTotalAmount(current.getTotalAmount())
          .withAverageAmount(current.getAverageAmount())
          .withNumberOfActiveCategories(current.getNumberOfActiveCategories())
          .withPrevious(previous)
          .build();
    }

    return current;
  }

  private CategoryKPIDto buildKPI(
      PeriodDashboardDto period, BigDecimal total, Integer activeCategories) {
    BigDecimal average =
        activeCategories > 0
            ? total.divide(BigDecimal.valueOf(activeCategories), 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
    return CategoryKPIDto.builder()
        .withPeriod(period)
        .withTotalAmount(total)
        .withAverageAmount(average)
        .withNumberOfActiveCategories(activeCategories)
        .build();
  }
}
