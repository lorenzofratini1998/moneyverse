package it.moneyverse.transaction.services;

import it.moneyverse.transaction.model.dto.*;
import it.moneyverse.transaction.model.dto.projection.CategoryTotalsAndCountsProjection;
import it.moneyverse.transaction.model.repositories.TransactionRepository;
import it.moneyverse.transaction.utils.PeriodResolver;
import it.moneyverse.transaction.utils.calculator.CategoryKpiCalculator;
import it.moneyverse.transaction.utils.calculator.CategoryStatisticsCalculator;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardManagementService implements DashboardService {

  private static final Logger LOGGER = LoggerFactory.getLogger(DashboardManagementService.class);

  private final PeriodResolver periodResolver;
  private final TransactionRepository transactionRepository;
  private final CategoryKpiCalculator kpiCalculator;
  private final CategoryStatisticsCalculator statisticsCalculator;

  public DashboardManagementService(
      PeriodResolver periodResolver,
      TransactionRepository transactionRepository,
      CategoryKpiCalculator kpiCalculator,
      CategoryStatisticsCalculator statisticsCalculator) {
    this.periodResolver = periodResolver;
    this.transactionRepository = transactionRepository;
    this.kpiCalculator = kpiCalculator;
    this.statisticsCalculator = statisticsCalculator;
  }

  @Override
  @Transactional(readOnly = true)
  public CategoryDashboardDto calculateCategoryDashboard(DashboardFilterRequestDto request) {
    DashboardFilterRequestDto filter = resolveRequest(request);

    CategoryTotalsAndCountsProjection totals =
        transactionRepository.getTotalsAndCategoryCountsByUserIdAndPeriod(
            filter.userId(),
            filter.accounts(),
            filter.categories(),
            filter.period().startDate(),
            filter.period().endDate(),
            filter.comparePeriod().map(PeriodDashboardDto::startDate).orElse(null),
            filter.comparePeriod().map(PeriodDashboardDto::endDate).orElse(null));

    CategoryKPIDto kpi = kpiCalculator.calculate(totals, filter.period(), filter.comparePeriod());
    List<CategoryStatisticsDto> stats = statisticsCalculator.calculate(filter, totals);

    return CategoryDashboardDto.builder()
        .withPeriod(filter.period())
        .withComparePeriod(filter.comparePeriod().orElse(null))
        .withKpi(kpi)
        .withTopCategory(stats.isEmpty() ? null : stats.getFirst())
        .withCategories(stats)
        .build();
  }

  private DashboardFilterRequestDto resolveRequest(DashboardFilterRequestDto request) {
    PeriodDashboardDto period = periodResolver.resolvePeriod(request.period());
    Optional<PeriodDashboardDto> comparePeriod =
        request.comparePeriod().map(periodResolver::resolvePeriod);
    return new DashboardFilterRequestDto(
        request.userId(), request.accounts(), request.categories(), period, comparePeriod);
  }
}
