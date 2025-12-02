package it.moneyverse.analytics.services;

import it.moneyverse.analytics.model.dto.FilterDto;
import it.moneyverse.analytics.model.dto.OverviewAnalyticsDto;
import it.moneyverse.analytics.model.projections.OverviewAnalyticsProjection;
import it.moneyverse.analytics.model.repositories.OverviewAnalyticsClickhouseDataAccess;
import it.moneyverse.analytics.services.strategies.AnalyticsStrategy;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class OverviewAnalyticsClickhouseService implements OverviewAnalyticsService {

  private final OverviewAnalyticsClickhouseDataAccess dataAccess;
  private final AnalyticsStrategy<List<OverviewAnalyticsDto>, List<OverviewAnalyticsProjection>>
      overviewStrategy;

  public OverviewAnalyticsClickhouseService(
      OverviewAnalyticsClickhouseDataAccess dataAccess,
      AnalyticsStrategy<List<OverviewAnalyticsDto>, List<OverviewAnalyticsProjection>>
          overviewStrategy) {
    this.dataAccess = dataAccess;
    this.overviewStrategy = overviewStrategy;
  }

  @Override
  public List<OverviewAnalyticsDto> calculateOverview(UUID userId) {
    FilterDto parameters = new FilterDto(userId, null, null, null, null, null, null);
    List<OverviewAnalyticsProjection> data = dataAccess.getOverviewAnalytics(parameters);
    return overviewStrategy.calculate(data, null, parameters);
  }
}
