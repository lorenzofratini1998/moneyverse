package it.moneyverse.analytics.runtime.controllers;

import it.moneyverse.analytics.model.dto.OverviewAnalyticsDto;
import it.moneyverse.analytics.model.dto.UserIdRequest;
import java.util.List;

public interface OverviewAnalyticsOperations {
  List<OverviewAnalyticsDto> calculateOverview(UserIdRequest request);
}
