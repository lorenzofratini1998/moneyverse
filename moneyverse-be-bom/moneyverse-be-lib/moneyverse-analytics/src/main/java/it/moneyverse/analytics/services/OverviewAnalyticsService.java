package it.moneyverse.analytics.services;

import it.moneyverse.analytics.model.dto.OverviewAnalyticsDto;
import java.util.List;
import java.util.UUID;

public interface OverviewAnalyticsService {
  List<OverviewAnalyticsDto> calculateOverview(UUID userId);
}
