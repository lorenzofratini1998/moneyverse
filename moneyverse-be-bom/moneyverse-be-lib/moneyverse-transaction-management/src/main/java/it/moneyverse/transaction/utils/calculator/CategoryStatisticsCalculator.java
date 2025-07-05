package it.moneyverse.transaction.utils.calculator;

import it.moneyverse.transaction.model.dto.CategoryStatisticsDto;
import it.moneyverse.transaction.model.dto.DashboardFilterRequestDto;
import it.moneyverse.transaction.model.dto.PeriodDashboardDto;
import it.moneyverse.transaction.model.dto.TrendDto;
import it.moneyverse.transaction.model.dto.projection.CategoryMonthlyStatsProjection;
import it.moneyverse.transaction.model.dto.projection.CategoryTotalsAndCountsProjection;
import it.moneyverse.transaction.model.repositories.TransactionRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class CategoryStatisticsCalculator {

  private final TransactionRepository transactionRepository;

  public CategoryStatisticsCalculator(TransactionRepository transactionRepository) {
    this.transactionRepository = transactionRepository;
  }

  public List<CategoryStatisticsDto> calculate(
      DashboardFilterRequestDto filter, CategoryTotalsAndCountsProjection totalsAndCountsCategory) {
    Map<UUID, List<CategoryMonthlyStatsProjection>> categoriesMonthlyStats =
        calculateCategoriesMonthlyStats(filter);

    return categoriesMonthlyStats.entrySet().parallelStream()
        .map(
            entry ->
                calculateCategoryStatistic(
                    entry.getKey(), entry.getValue(), totalsAndCountsCategory, filter))
        .sorted(Comparator.comparing(CategoryStatisticsDto::getAmount).reversed())
        .toList();
  }

  /*private List<CategoryStatisticsDto> calculateCategoryStatistics(
      UUID userId,
      UUID accountId,
      UUID categoryId,
      CategoryTotalsAndCountsProjection totalsAndCountsCategory,
      PeriodDashboardDto period) {
    return calculateCategoryStatistics(
        userId, accountId, categoryId, totalsAndCountsCategory, period, null);
  }

  private List<CategoryStatisticsDto> calculateCategoryStatistics(
      DashboardFilterRequestDto filter, CategoryTotalsAndCountsProjection totalsAndCountsCategory) {

    Map<UUID, List<CategoryMonthlyStatsProjection>> categoriesMonthlyStats =
        calculateCategoriesMonthlyStats(filter);

    return categoriesMonthlyStats.entrySet().parallelStream()
        .map(
            entry ->
                calculateCategoryStatistic(
                    entry.getKey(),
                    entry.getValue(),
                    totalsAndCountsCategory,
                    filter))
        .sorted(Comparator.comparing(CategoryStatisticsDto::getAmount).reversed())
        .toList();
  }*/

  private Map<UUID, List<CategoryMonthlyStatsProjection>> calculateCategoriesMonthlyStats(
      DashboardFilterRequestDto filter) {
    LocalDate start =
        filter
            .comparePeriod()
            .map(PeriodDashboardDto::startDate)
            .orElse(filter.period().startDate());
    return transactionRepository
        .getMonthlyTotalsByCategory(
            filter.userId(),
            filter.accounts(),
            filter.categories(),
            start,
            filter.period().endDate())
        .stream()
        .collect(
            Collectors.groupingBy(
                CategoryMonthlyStatsProjection::getCategoryId,
                LinkedHashMap::new,
                Collectors.toList()));
  }

  private CategoryStatisticsDto calculateCategoryStatistic(
      UUID categoryId,
      List<CategoryMonthlyStatsProjection> monthlyStats,
      CategoryTotalsAndCountsProjection totals,
      DashboardFilterRequestDto filter) {

    PeriodDashboardDto period = filter.period();
    BigDecimal categoryCurrentTotal = sumCategoryAmountForPeriod(monthlyStats, period);
    List<TrendDto> currentTrend = buildTrend(monthlyStats, period, categoryCurrentTotal);

    CategoryStatisticsDto.Builder builder =
        CategoryStatisticsDto.builder()
            .withCategoryId(categoryId)
            .withPeriod(period)
            .withAmount(categoryCurrentTotal)
            .withPercentage(calculatePercentage(categoryCurrentTotal, totals.getCurrentTotal()))
            .withData(currentTrend);

    if (filter.comparePeriod().isPresent()) {
      PeriodDashboardDto comparePeriod = filter.comparePeriod().get();
      BigDecimal categoryPreviousTotal = sumCategoryAmountForPeriod(monthlyStats, comparePeriod);
      List<TrendDto> previousTrend =
          buildTrend(monthlyStats, comparePeriod, totals.getPreviousTotal());
      builder.withPrevious(
          CategoryStatisticsDto.builder()
              .withCategoryId(categoryId)
              .withPeriod(comparePeriod)
              .withAmount(categoryPreviousTotal)
              .withPercentage(calculatePercentage(categoryPreviousTotal, totals.getPreviousTotal()))
              .withData(previousTrend)
              .build());
    }
    return builder.build();
  }

  private BigDecimal sumCategoryAmountForPeriod(
      List<CategoryMonthlyStatsProjection> monthlyStats, PeriodDashboardDto period) {
    return monthlyStats.stream()
        .filter(p -> isInPeriod(p, period))
        .map(CategoryMonthlyStatsProjection::getTotal)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private List<TrendDto> buildTrend(
      List<CategoryMonthlyStatsProjection> stats,
      PeriodDashboardDto period,
      BigDecimal categoryTotal) {
    return stats.stream()
        .filter(p -> isInPeriod(p, period))
        .sorted(Comparator.comparing(p -> YearMonth.of(p.getYear(), p.getMonth())))
        .map(
            p -> {
              BigDecimal amount = p.getTotal();
              BigDecimal percentage = calculatePercentage(amount, categoryTotal);
              return TrendDto.builder()
                  .withPeriod(YearMonth.of(p.getYear(), p.getMonth()))
                  .withAmount(amount)
                  .withPercentage(percentage)
                  .build();
            })
        .toList();
  }

  private boolean isInPeriod(CategoryMonthlyStatsProjection proj, PeriodDashboardDto period) {
    YearMonth actual = YearMonth.of(proj.getYear(), proj.getMonth());
    YearMonth start = YearMonth.from(period.startDate());
    YearMonth end = YearMonth.from(period.endDate());
    return !actual.isBefore(start) && !actual.isAfter(end);
  }

  private BigDecimal calculatePercentage(BigDecimal amount, BigDecimal total) {
    if (total.signum() == 0) {
      return BigDecimal.ZERO;
    }
    return amount.divide(total, 4, RoundingMode.HALF_UP);
  }
}
