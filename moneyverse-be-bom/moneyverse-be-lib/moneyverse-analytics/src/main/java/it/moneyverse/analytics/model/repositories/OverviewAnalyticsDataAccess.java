package it.moneyverse.analytics.model.repositories;

import it.moneyverse.analytics.model.dto.FilterDto;
import it.moneyverse.analytics.model.projections.OverviewAnalyticsProjection;
import java.util.List;

public interface OverviewAnalyticsDataAccess {
    List<OverviewAnalyticsProjection> getOverviewAnalytics(FilterDto parameters);
}
